/*
 * Copyright 2013 the original author or authors.
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

package org.gradle.api.internal.file.copy;

import com.google.common.annotations.VisibleForTesting;
import org.gradle.api.Action;
import org.gradle.api.NonExtensible;
import org.gradle.api.Transformer;
import org.gradle.api.file.CopyProcessingSpec;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.ExpandDetails;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.specs.Spec;


import javax.annotation.Nullable;
import javax.inject.Inject;
import java.io.FilterReader;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Wraps another CopySpec impl, only exposing the CopySpec API.
 *
 * Prevents users from accessing "internal" methods on implementations.
 */
@NonExtensible
public class CopySpecWrapper implements CopySpec {

    @VisibleForTesting
    final CopySpec delegate;

    @Inject
    public CopySpecWrapper(CopySpec delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isCaseSensitive() {
        return delegate.isCaseSensitive();
    }

    @Override
    public void setCaseSensitive(boolean caseSensitive) {
        delegate.setCaseSensitive(caseSensitive);
    }

    @Override
    public boolean getIncludeEmptyDirs() {
        return delegate.getIncludeEmptyDirs();
    }

    @Override
    public void setIncludeEmptyDirs(boolean includeEmptyDirs) {
        delegate.setIncludeEmptyDirs(includeEmptyDirs);
    }

    @Override
    public DuplicatesStrategy getDuplicatesStrategy() {
        return delegate.getDuplicatesStrategy();
    }

    @Override
    public void setDuplicatesStrategy(DuplicatesStrategy strategy) {
        delegate.setDuplicatesStrategy(strategy);
    }

    @Override
    public CopySpec filesMatching(String pattern, Action<? super FileCopyDetails> action) {
        delegate.filesMatching(pattern, action);
        return this;
    }

    @Override
    public CopySpec filesMatching(Iterable<String> patterns, Action<? super FileCopyDetails> action) {
        delegate.filesMatching(patterns, action);
        return this;
    }

    @Override
    public CopySpec filesNotMatching(String pattern, Action<? super FileCopyDetails> action) {
        delegate.filesNotMatching(pattern, action);
        return this;
    }

    @Override
    public CopySpec filesNotMatching(Iterable<String> patterns, Action<? super FileCopyDetails> action) {
        delegate.filesNotMatching(patterns, action);
        return this;
    }

    @Override
    public CopySpec with(CopySpec... sourceSpecs) {
        delegate.with(sourceSpecs);
        return this;
    }

    @Override
    public CopySpec from(Object... sourcePaths) {
        delegate.from(sourcePaths);
        return this;
    }



    @Override
    public CopySpec from(Object sourcePath, Action<? super CopySpec> configureAction) {
        return delegate.from(sourcePath, configureAction);
    }

    @Override
    public CopySpec setIncludes(Iterable<String> includes) {
        delegate.setIncludes(includes);
        return this;
    }

    @Override
    public CopySpec setExcludes(Iterable<String> excludes) {
        delegate.setExcludes(excludes);
        return this;
    }

    @Override
    public CopySpec include(String... includes) {
        delegate.include(includes);
        return this;
    }

    @Override
    public CopySpec include(Iterable<String> includes) {
        delegate.include(includes);
        return this;
    }

    @Override
    public CopySpec include(Spec<FileTreeElement> includeSpec) {
        delegate.include(includeSpec);
        return this;
    }


    @Override
    public CopySpec exclude(String... excludes) {
        delegate.exclude(excludes);
        return this;
    }

    @Override
    public CopySpec exclude(Iterable<String> excludes) {
        delegate.exclude(excludes);
        return this;
    }

    @Override
    public CopySpec exclude(Spec<FileTreeElement> excludeSpec) {
        delegate.exclude(excludeSpec);
        return this;
    }



    @Override
    public CopySpec into(Object destPath) {
        delegate.into(destPath);
        return this;
    }



    @Override
    public CopySpec into(Object destPath, Action<? super CopySpec> copySpec) {
        return delegate.into(destPath, copySpec);
    }


    @Override
    public CopySpec rename(Transformer<String, String> renamer) {
        delegate.rename(renamer);
        return this;
    }

    @Override
    public CopySpec rename(String sourceRegEx, String replaceWith) {
        delegate.rename(sourceRegEx, replaceWith);
        return this;
    }

    @Override
    public CopyProcessingSpec rename(Pattern sourceRegEx, String replaceWith) {
        delegate.rename(sourceRegEx, replaceWith);
        return this;
    }

    @Override
    public CopySpec filter(Map<String, ?> properties, Class<? extends FilterReader> filterType) {
        delegate.filter(properties, filterType);
        return this;
    }

    @Override
    public CopySpec filter(Class<? extends FilterReader> filterType) {
        delegate.filter(filterType);
        return this;
    }



    @Override
    public CopySpec filter(Transformer<String, String> transformer) {
        delegate.filter(transformer);
        return this;
    }

    @Override
    public CopySpec expand(Map<String, ?> properties) {
        delegate.expand(properties);
        return this;
    }

    @Override
    public CopySpec expand(Map<String, ?> properties, Action<? super ExpandDetails> action) {
        delegate.expand(properties, action);
        return this;
    }

    @Override
    public CopySpec eachFile(Action<? super FileCopyDetails> action) {
        delegate.eachFile(action);
        return this;
    }



    @Override
    public Integer getFileMode() {
        return delegate.getFileMode();
    }

    @Override
    public CopyProcessingSpec setFileMode(@Nullable Integer mode) {
        delegate.setFileMode(mode);
        return this;
    }

    @Override
    public Integer getDirMode() {
        return delegate.getDirMode();
    }

    @Override
    public CopyProcessingSpec setDirMode(@Nullable Integer mode) {
        delegate.setDirMode(mode);
        return this;
    }

    @Override
    public Set<String> getIncludes() {
        return delegate.getIncludes();
    }

    @Override
    public Set<String> getExcludes() {
        return delegate.getExcludes();
    }

    @Override
    public String getFilteringCharset() {
        return delegate.getFilteringCharset();
    }

    @Override
    public void setFilteringCharset(String charset) {
        delegate.setFilteringCharset(charset);
    }
}
