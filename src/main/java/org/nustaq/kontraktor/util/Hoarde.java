/*
Kontraktor Copyright (c) Ruediger Moeller, All rights reserved.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3.0 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

See https://www.gnu.org/licenses/lgpl.txt
*/

package org.nustaq.kontraktor.util;

import org.nustaq.kontraktor.*;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by ruedi on 07.09.14.
 *
 * a utility class allowing to address/manage multiple actors of same type
 *
 */
public class Hoarde<T extends Actor> {

    Actor actors[];
    int index = 0;
    Promise prev;

    /**
     * create a hoarde with each actor having a dedicated thread
     * @param numActors
     * @param actor
     */
    public Hoarde(int numActors, Class<T> actor) {
        actors = new Actor[numActors];
        for (int i = 0; i < actors.length; i++) {
            actors[i] = Actors.asActor(actor);
        }
    }

    /**
     * create a hoarde scheduled on given scheduler
     * @param numActors
     * @param actor
     * @param sched
     */
    public Hoarde(int numActors, Class<T> actor, Scheduler sched) {
        actors = new Actor[numActors];
        for (int i = 0; i < actors.length; i++) {
            actors[i] = Actors.asActor(actor, sched);
        }
    }

    public <X> IPromise<T>[] map(BiFunction<T, Integer, IPromise<X>> init) {
        IPromise res[] = new IPromise[actors.length];
        for (int i = 0; i < actors.length; i++) {
            T actor = (T) actors[i];
            res[i] = init.apply(actor,i);
        }
        return res;
    }

    /**
     * iterate over each actor and execute tocall. E.g. hoarde.each( actor -> actor.init() )
     * @param tocall
     * @return
     */
    public Hoarde<T> each(Consumer<T> tocall) {
        for (int i = 0; i < actors.length; i++) {
            tocall.accept( (T) actors[i] );
        }
        return this;
    }

    /**
     * same as other each but with index
     *
     * @param init
     * @return
     */
    public Hoarde<T> each(BiConsumer<T, Integer> init) {
        for (int i = 0; i < actors.length; i++) {
            init.accept( (T) actors[i], i );
        }
        return this;
    }

    /**
     * calls given function round robin. typical use:
     *
     * hoarde.ordered( actor -> actor.decode(byte[]) ).onResult( decodedObj -> businesslogic(decodedObj) );
     *
     * after
     * @param toCall
     * @return
     */
    public IPromise ordered(Function<T, IPromise> toCall) {
        final IPromise result = toCall.apply((T) actors[index]);
        index++;
        if (index==actors.length)
            index = 0;
        if ( prev == null ) {
            prev = new Promise();
            result.then(prev);
            return prev;
        } else {
            Promise p = new Promise();
            prev.getNext().finallyDo((res, err) -> result.then((res1, err1) -> p.complete(res1, err1)));
            prev = p;
            return p;
        }
    }

    public int getSize() {
        return actors.length;
    }

    public T getActor(int i) {
        return (T) actors[i];
    }

}
