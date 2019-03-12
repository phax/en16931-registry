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

import com.helger.photon.basic.app.menu.IMenuItemPage;
import com.helger.photon.basic.app.menu.IMenuTree;
import com.helger.photon.bootstrap4.pages.BootstrapPagesMenuConfigurator;
import com.helger.photon.bootstrap4.pages.security.BasePageSecurityChangePassword;
import com.helger.photon.security.menu.MenuObjectFilterUserAssignedToUserGroup;
import com.helger.photon.uicore.page.WebPageExecutionContext;
import com.helger.photon.uicore.page.system.BasePageShowChildren;
import com.helger.registry434.page.PageSecureCIUSDetails;
import com.helger.registry434.page.PageSecureCEHeader;

@Immutable
public final class AppMenuSecure
{
  private AppMenuSecure ()
  {}

  public static void init (@Nonnull final IMenuTree aMenuTree)
  {
    // We need this additional indirection layer, as the pages are initialized
    // statically!
    final MenuObjectFilterUserAssignedToUserGroup aFilterSuperUser = new MenuObjectFilterUserAssignedToUserGroup (CAppSecurity.USERGROUPID_SUPERUSER);

    {
      aMenuTree.createRootItem (new PageSecureCEHeader (CMenuSecure.MENU_CE_HEADER));
      aMenuTree.createRootItem (new PageSecureCIUSDetails (CMenuSecure.MENU_CE_DETAILS));
    }

    // Administrator
    {
      final IMenuItemPage aAdmin = aMenuTree.createRootItem (new BasePageShowChildren <WebPageExecutionContext> (CMenuSecure.MENU_ADMIN,
                                                                                                                 "Administration",
                                                                                                                 aMenuTree));
      aMenuTree.createItem (aAdmin,
                            new BasePageSecurityChangePassword <WebPageExecutionContext> (CMenuSecure.MENU_ADMIN_CHANGE_PASSWORD));
      BootstrapPagesMenuConfigurator.addAllItems (aMenuTree, aAdmin, aFilterSuperUser, CApp.DEFAULT_LOCALE);
    }

    // Default menu item
    aMenuTree.setDefaultMenuItemID (CMenuSecure.MENU_CE_HEADER);
  }
}
