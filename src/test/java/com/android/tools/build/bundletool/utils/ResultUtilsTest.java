/*
 * Copyright (C) 2018 The Android Open Source Project
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
 * limitations under the License
 */

package com.android.tools.build.bundletool.utils;

import static com.android.tools.build.bundletool.testing.ApksArchiveHelpers.createApkDescription;
import static com.android.tools.build.bundletool.testing.ApksArchiveHelpers.createInstantApkSet;
import static com.android.tools.build.bundletool.testing.ApksArchiveHelpers.createMasterApkDescription;
import static com.android.tools.build.bundletool.testing.ApksArchiveHelpers.createSplitApkSet;
import static com.android.tools.build.bundletool.testing.ApksArchiveHelpers.createStandaloneApkSet;
import static com.android.tools.build.bundletool.testing.ApksArchiveHelpers.createVariant;
import static com.android.tools.build.bundletool.testing.TargetingUtils.apkAbiTargeting;
import static com.android.tools.build.bundletool.testing.TargetingUtils.sdkVersionFrom;
import static com.android.tools.build.bundletool.testing.TargetingUtils.variantSdkTargeting;
import static com.google.common.truth.Truth.assertThat;

import com.android.bundle.Commands.BuildApksResult;
import com.android.bundle.Commands.Variant;
import com.android.bundle.Targeting.Abi.AbiAlias;
import com.android.bundle.Targeting.ApkTargeting;
import com.android.bundle.Targeting.SdkVersion;
import com.google.common.collect.ImmutableSet;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ResultUtilsTest {

  @Test
  public void filterInstantApkVariant() throws Exception {
    Variant standaloneVariant = createStandaloneVariant();
    Variant splitVariant = createSplitVariant();
    Variant instantVariant = createInstantVariant();
    BuildApksResult apksResult =
        BuildApksResult.newBuilder()
            .addVariant(standaloneVariant)
            .addVariant(splitVariant)
            .addVariant(instantVariant)
            .build();

    assertThat(ResultUtils.instantApkVariants(apksResult)).containsExactly(instantVariant);
  }

  @Test
  public void filterSplitApkVariant() throws Exception {
    Variant standaloneVariant = createStandaloneVariant();
    Variant splitVariant = createSplitVariant();
    Variant instantVariant = createInstantVariant();
    BuildApksResult apksResult =
        BuildApksResult.newBuilder()
            .addVariant(standaloneVariant)
            .addVariant(splitVariant)
            .addVariant(instantVariant)
            .build();

    assertThat(ResultUtils.splitApkVariants(apksResult)).containsExactly(splitVariant);
  }

  @Test
  public void filterStandaloneApkVariant() throws Exception {
    Variant standaloneVariant = createStandaloneVariant();
    Variant splitVariant = createSplitVariant();
    Variant instantVariant = createInstantVariant();
    BuildApksResult apksResult =
        BuildApksResult.newBuilder()
            .addVariant(standaloneVariant)
            .addVariant(splitVariant)
            .addVariant(instantVariant)
            .build();

    assertThat(ResultUtils.standaloneApkVariants(apksResult)).containsExactly(standaloneVariant);
  }

  @Test
  public void isInstantApkVariantTrue() throws Exception {
    Variant variant = createInstantVariant();

    assertThat(ResultUtils.isInstantApkVariant(variant)).isTrue();
    assertThat(ResultUtils.isSplitApkVariant(variant)).isFalse();
    assertThat(ResultUtils.isStandaloneApkVariant(variant)).isFalse();
  }

  @Test
  public void isStandaloneApkVariantTrue() throws Exception {
    Variant variant = createStandaloneVariant();

    assertThat(ResultUtils.isStandaloneApkVariant(variant)).isTrue();
    assertThat(ResultUtils.isSplitApkVariant(variant)).isFalse();
    assertThat(ResultUtils.isInstantApkVariant(variant)).isFalse();
  }

  @Test
  public void isSplitApkVariantTrue() throws Exception {
    Variant variant = createSplitVariant();

    assertThat(ResultUtils.isSplitApkVariant(variant)).isTrue();
    assertThat(ResultUtils.isStandaloneApkVariant(variant)).isFalse();
    assertThat(ResultUtils.isInstantApkVariant(variant)).isFalse();
  }

  private Variant createInstantVariant() {
    Path apkLBase = Paths.get("instant", "apkL-base.apk");
    Path apkLFeature = Paths.get("instant", "apkL-feature.apk");
    Path apkLOther = Paths.get("instant", "apkL-other.apk");
    return createVariant(
        variantSdkTargeting(sdkVersionFrom(21), ImmutableSet.of(SdkVersion.getDefaultInstance())),
        createInstantApkSet("base", ApkTargeting.getDefaultInstance(), apkLBase),
        createInstantApkSet("feature", ApkTargeting.getDefaultInstance(), apkLFeature),
        createInstantApkSet("other", ApkTargeting.getDefaultInstance(), apkLOther));
  }

  private Variant createSplitVariant() {
    Path apkL = Paths.get("splits", "apkL.apk");
    Path apkLx86 = Paths.get("splits", "apkL-x86.apk");
    return createVariant(
        variantSdkTargeting(sdkVersionFrom(21)),
        createSplitApkSet(
            "base",
            createMasterApkDescription(ApkTargeting.getDefaultInstance(), apkL),
            createApkDescription(
                apkAbiTargeting(AbiAlias.X86, ImmutableSet.of()),
                apkLx86,
                /* isMasterSplit= */ false)));
  }

  private Variant createStandaloneVariant() {
    Path apkPreL = Paths.get("apkPreL.apk");
    return createVariant(
        variantSdkTargeting(sdkVersionFrom(21), ImmutableSet.of(SdkVersion.getDefaultInstance())),
        createStandaloneApkSet(ApkTargeting.getDefaultInstance(), apkPreL));
  }
}
