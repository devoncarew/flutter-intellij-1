/*
 * Copyright 2017 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package io.flutter.project;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import io.flutter.testing.FlutterModuleFixture;
import io.flutter.testing.ProjectFixture;
import io.flutter.testing.Testing;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ProjectWatchTest {
  @Rule
  public ProjectFixture projectFixture = Testing.makeCodeInsightModule();

  @Rule
  public FlutterModuleFixture flutterFixture = new FlutterModuleFixture(projectFixture);

  @Test
  public void shouldSendEventWhenProjectCloses() throws Exception {
    Testing.runOnDispatchThread(() -> {
      final AtomicInteger callCount = new AtomicInteger();
      final ProjectWatch listen = ProjectWatch.subscribe(projectFixture.getProject(), callCount::incrementAndGet);

      ProjectManager.getInstance().closeProject(projectFixture.getProject());
      // The number of events fired is an implementation detail of the project manager. We just need at least one.
      assertNotEquals(0, callCount.get());
    });
  }

  @Test
  public void shouldSendEventWhenModuleRootsChange() throws Exception {
    Testing.runOnDispatchThread(() -> {
      final AtomicInteger callCount = new AtomicInteger();
      final ProjectWatch listen = ProjectWatch.subscribe(projectFixture.getProject(), callCount::incrementAndGet);

      ModuleRootModificationUtil.addContentRoot(projectFixture.getModule(), "testDir");
      assertEquals(1, callCount.get());
    });
  }
}
