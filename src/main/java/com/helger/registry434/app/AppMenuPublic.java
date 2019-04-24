/**
 * Copyright (C) 2019 Philip Helger
 * http://www.helger.com
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.registry434.app;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.helger.photon.core.menu.IMenuTree;
import com.helger.registry434.page.PagePublicCEHeaderList;

@Immutable
public final class AppMenuPublic
{
  private AppMenuPublic ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // Show all
    aMenuTree.createRootItem (new PagePublicCEHeaderList (CMenuPublic.MENU_CE_HEADER_LIST));

    // Default menu item
    aMenuTree.setDefaultMenuItemID (CMenuPublic.MENU_CE_HEADER_LIST);
  }
}
