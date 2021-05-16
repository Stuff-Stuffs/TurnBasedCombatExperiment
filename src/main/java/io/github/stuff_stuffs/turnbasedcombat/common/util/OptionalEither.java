package io.github.stuff_stuffs.turnbasedcombat.common.util;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class OptionalEither<L, R> {
    private OptionalEither() {
    }

    public abstract Optional<L> getLeft();

    public abstract Optional<R> getRight();

    public abstract boolean isLeft();

    public abstract boolean isRight();

    public abstract <U> OptionalEither<U, R> mapLeft(Function<? super L, ? extends U> function);

    public abstract <U> OptionalEither<L, U> mapRight(Function<? super R, ? extends U> function);

    public abstract <U> Optional<U> map(Function<? super L, ? extends U> leftFunction, Function<? super R, ? extends U> rightFunction);

    public abstract void ifLeft(Consumer<? super L> consumer);

    public abstract void ifRight(Consumer<? super R> consumer);

    public abstract void consume(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer, Runnable absent);

    public abstract OptionalEither<R, L> swap();

    public static <L, R> OptionalEither<L, R> left(@NotNull final L left) {
        return new OptionalEither<L, R>() {
            @Override
            public Optional<L> getLeft() {
                return Optional.of(left);
            }

            @Override
            public Optional<R> getRight() {
                return Optional.empty();
            }

            @Override
            public boolean isLeft() {
                return true;
            }

            @Override
            public boolean isRight() {
                return false;
            }

            @Override
            public <U> OptionalEither<U, R> mapLeft(final Function<? super L, ? extends U> function) {
                return OptionalEither.left(function.apply(left));
            }

            @Override
            public <U> OptionalEither<L, U> mapRight(final Function<? super R, ? extends U> function) {
                return (OptionalEither<L, U>) this;
            }

            @Override
            public <U> Optional<U> map(final Function<? super L, ? extends U> leftFunction, final Function<? super R, ? extends U> rightFunction) {
                return Optional.ofNullable(leftFunction.apply(left));
            }

            @Override
            public void ifLeft(final Consumer<? super L> consumer) {
                consumer.accept(left);
            }

            @Override
            public void ifRight(final Consumer<? super R> consumer) {

            }

            @Override
            public void consume(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer, Runnable absent) {
                leftConsumer.accept(left);
            }

            @Override
            public OptionalEither<R, L> swap() {
                return OptionalEither.right(left);
            }
        };
    }

    public static <L, R> OptionalEither<L, R> right(@NotNull final R right) {
        return new OptionalEither<L, R>() {
            @Override
            public Optional<L> getLeft() {
                return Optional.empty();
            }

            @Override
            public Optional<R> getRight() {
                return Optional.of(right);
            }

            @Override
            public boolean isLeft() {
                return false;
            }

            @Override
            public boolean isRight() {
                return true;
            }

            @Override
            public <U> OptionalEither<U, R> mapLeft(final Function<? super L, ? extends U> function) {
                return (OptionalEither<U, R>) this;
            }

            @Override
            public <U> OptionalEither<L, U> mapRight(Function<? super R, ? extends U> function) {
                return OptionalEither.right(function.apply(right));
            }

            @Override
            public <U> Optional<U> map(final Function<? super L, ? extends U> leftFunction, final Function<? super R, ? extends U> rightFunction) {
                return Optional.empty();
            }

            @Override
            public void ifLeft(final Consumer<? super L> consumer) {

            }

            @Override
            public void ifRight(final Consumer<? super R> consumer) {
                consumer.accept(right);
            }

            @Override
            public void consume(Consumer<? super L> leftConsumer, Consumer<? super R> rightConsumer, Runnable absent) {
                rightConsumer.accept(right);
            }

            @Override
            public OptionalEither<R, L> swap() {
                return OptionalEither.left(right);
            }
        };
    }

    private static final OptionalEither<?, ?> NEITHER = new OptionalEither<Object, Object>() {
        @Override
        public Optional<Object> getLeft() {
            return Optional.empty();
        }

        @Override
        public Optional<Object> getRight() {
            return Optional.empty();
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public <U> OptionalEither<U, Object> mapLeft(final Function<? super Object, ? extends U> function) {
            return (OptionalEither<U, Object>) this;
        }

        @Override
        public <U> OptionalEither<Object, U> mapRight(final Function<? super Object, ? extends U> function) {
            return (OptionalEither<Object, U>) this;
        }

        @Override
        public <U> Optional<U> map(final Function<? super Object, ? extends U> leftFunction, final Function<? super Object, ? extends U> rightFunction) {
            return Optional.empty();
        }

        @Override
        public void ifLeft(final Consumer<? super Object> consumer) {

        }

        @Override
        public void ifRight(final Consumer<? super Object> consumer) {

        }

        @Override
        public void consume(Consumer<? super Object> leftConsumer, Consumer<? super Object> rightConsumer, Runnable absent) {
            absent.run();
        }

        @Override
        public OptionalEither<Object, Object> swap() {
            return this;
        }
    };

    public static <L, R> OptionalEither<L, R> neither() {
        return (OptionalEither<L, R>) NEITHER;
    }
}
