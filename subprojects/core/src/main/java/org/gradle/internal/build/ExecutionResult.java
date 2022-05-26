/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.internal.build;

import com.google.common.collect.ImmutableList;
import org.gradle.execution.MultipleBuildFailures;
import org.gradle.internal.Cast;

import java.util.Collections;
import java.util.List;

public abstract class ExecutionResult<T> {
    private static final Success<Void> SUCCESS = new Success<Void>() {
        @Override
        public Void getValue() {
            return null;
        }
    };

    /**
     * Returns the value, if available.
     */
    public abstract T getValue();

    /**
     * Returns the failures in this result, or an empty list if the operation was successful.
     */
    public abstract List<Throwable> getFailures();

    /**
     * Returns a single exception object that contains all failures in this result, if available.
     */
    public abstract RuntimeException getFailure();

    /**
     * Returns the value or rethrows the failures of this result.
     */
    public abstract T getValueOrRethrow();

    /**
     * Rethrows the failures in this result, if any, otherwise does nothing.
     */
    public abstract void rethrow();

    /**
     * Returns a copy of this result, adding any failures from the given result object.
     */
    public abstract ExecutionResult<T> withFailures(ExecutionResult<Void> otherResult);

    /**
     * Casts a failed result.
     */
    public abstract <S> ExecutionResult<S> asFailure();

    public static <T> ExecutionResult<T> succeeded(T value) {
        return new Success<T>() {
            @Override
            public T getValue() {
                return value;
            }
        };
    }

    public static ExecutionResult<Void> succeeded() {
        return SUCCESS;
    }

    public static <T> ExecutionResult<T> failed(Throwable failure) {
        return new Failure<>(ImmutableList.of(failure));
    }

    public static <T> ExecutionResult<Void> maybeFailed(List<? extends Throwable> failures) {
        if (failures.isEmpty()) {
            return SUCCESS;
        } else {
            return new Failure<>(ImmutableList.copyOf(failures));
        }
    }

    private static abstract class Success<T> extends ExecutionResult<T> {
        @Override
        public List<Throwable> getFailures() {
            return Collections.emptyList();
        }

        @Override
        public ExecutionResult<T> withFailures(ExecutionResult<Void> otherResult) {
            if (otherResult.getFailures().isEmpty()) {
                return this;
            }
            return otherResult.asFailure();
        }

        @Override
        public T getValueOrRethrow() {
            return getValue();
        }

        @Override
        public void rethrow() {
        }

        @Override
        public RuntimeException getFailure() {
            throw new IllegalArgumentException("Cannot get the failure of a successful result.");
        }

        @Override
        public <S> ExecutionResult<S> asFailure() {
            throw new IllegalArgumentException("Cannot cast a successful result to a failed result.");
        }
    }

    private static class Failure<T> extends ExecutionResult<T> {
        private final ImmutableList<Throwable> failures;

        public Failure(ImmutableList<Throwable> failures) {
            this.failures = failures;
        }

        @Override
        public T getValue() {
            throw new IllegalArgumentException("Cannot get the value of a failed result.");
        }

        @Override
        public T getValueOrRethrow() {
            rethrow();
            return null;
        }

        @Override
        public List<Throwable> getFailures() {
            return failures;
        }

        @Override
        public RuntimeException getFailure() {
            if (failures.size() == 1 && failures.get(0) instanceof RuntimeException) {
                return (RuntimeException) failures.get(0);
            }
            return new MultipleBuildFailures(failures);
        }

        @Override
        public void rethrow() {
            throw getFailure();
        }

        @Override
        public ExecutionResult<T> withFailures(ExecutionResult<Void> otherResult) {
            if (otherResult.getFailures().isEmpty()) {
                return this;
            }
            ImmutableList.Builder<Throwable> builder = ImmutableList.builder();
            builder.addAll(failures);
            builder.addAll(otherResult.getFailures());
            return new Failure<>(builder.build());
        }

        @Override
        public <S> ExecutionResult<S> asFailure() {
            return Cast.uncheckedCast(this);
        }
    }
}
