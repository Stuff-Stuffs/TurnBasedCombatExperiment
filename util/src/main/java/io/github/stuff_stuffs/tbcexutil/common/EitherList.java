package io.github.stuff_stuffs.tbcexutil.common;

import com.google.common.collect.AbstractIterator;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public final class EitherList<L, R> extends AbstractList<Either<L, R>> {
    private final ReferenceArrayList<Either<L, R>> backing;

    public EitherList(final int size) {
        backing = new ReferenceArrayList<>(size);
    }

    public EitherList() {
        backing = new ReferenceArrayList<>();
    }

    public void addLeft(final L l) {
        add(Either.left(l));
    }

    public void addRight(final R r) {
        add(Either.right(r));
    }

    @Override
    public void add(final int index, final Either<L, R> element) {
        backing.add(index, element);
    }

    public Either<L, R> setLeft(final int index, final L l) {
        return set(index, Either.left(l));
    }

    public Either<L, R> setRight(final int index, final R r) {
        return set(index, Either.right(r));
    }

    @Override
    public Either<L, R> set(final int index, final Either<L, R> element) {
        return backing.set(index, element);
    }

    @Override
    public Either<L, R> get(final int index) {
        return backing.get(index);
    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    protected void removeRange(final int fromIndex, final int toIndex) {
        backing.removeElements(fromIndex, toIndex);
    }

    @Override
    public Either<L, R> remove(final int index) {
        return backing.remove(index);
    }

    public Iterator<L> filterLeft() {
        return new AbstractIterator<>() {
            private final Iterator<Either<L, R>> iterator = backing.iterator();

            @Override
            protected L computeNext() {
                while (iterator.hasNext()) {
                    final Either<L, R> either = iterator.next();
                    final Optional<L> left = either.left();
                    if (left.isPresent()) {
                        return left.get();
                    }
                }
                return endOfData();
            }
        };
    }

    public Iterator<R> filterRight() {
        return new AbstractIterator<>() {
            private final Iterator<Either<L, R>> iterator = backing.iterator();

            @Override
            protected R computeNext() {
                while (iterator.hasNext()) {
                    final Either<L, R> either = iterator.next();
                    final Optional<R> right = either.right();
                    if (right.isPresent()) {
                        return right.get();
                    }
                }
                return endOfData();
            }
        };
    }

    public <T> Iterator<T> map(final Function<? super L, ? extends T> leftFunc, final Function<? super R, ? extends T> rightFunc) {
        return new AbstractIterator<>() {
            private final Iterator<Either<L, R>> iterator = backing.iterator();

            @Override
            protected T computeNext() {
                if (iterator.hasNext()) {
                    final Either<L, R> either = iterator.next();
                    return either.map(leftFunc, rightFunc);
                }
                return endOfData();
            }
        };
    }
}
