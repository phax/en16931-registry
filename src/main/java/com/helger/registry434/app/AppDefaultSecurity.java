/**
 * Copyright (C) 2019-2020 Philip Helger
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

import javax.annotation.concurrent.Immutable;

import com.helger.photon.security.CSecurity;
import com.helger.photon.security.mgr.PhotonSecurityManager;
import com.helger.photon.security.role.RoleManager;
import com.helger.photon.security.user.UserManager;
import com.helger.photon.security.usergroup.UserGroupManager;

@Immutable
public final class AppDefaultSecurity
{
  private AppDefaultSecurity ()
  {}

  public static void init ()
  {
    final UserManager aUserMgr = PhotonSecurityManager.getUserMgr ();
    final UserGroupManager aUserGroupMgr = PhotonSecurityManager.getUserGroupMgr ();
    final RoleManager aRoleMgr = PhotonSecurityManager.getRoleMgr ();

    // Standard users
    if (!aUserMgr.containsWithID (CSecurity.USER_ADMINISTRATOR_ID))
      aUserMgr.createPredefinedUser (CSecurity.USER_ADMINISTRATOR_ID,
                                     CSecurity.USER_ADMINISTRATOR_EMAIL,
                                     CSecurity.USER_ADMINISTRATOR_EMAIL,
                                     CSecurity.USER_ADMINISTRATOR_PASSWORD,
                                     CSecurity.USER_ADMINISTRATOR_NAME,
                                     null,
                                     null,
                                     null,
                                     null,
                                     false);

    // Create all roles
    if (!aRoleMgr.containsWithID (CAppSecurity.ROLEID_WRITE))
      aRoleMgr.createPredefinedRole (CAppSecurity.ROLEID_WRITE, "write", null, null);
    if (!aRoleMgr.containsWithID (CAppSecurity.ROLEID_READ))
      aRoleMgr.createPredefinedRole (CAppSecurity.ROLEID_READ, "read", null, null);

    // User group Administrators
    if (!aUserGroupMgr.containsWithID (CAppSecurity.USERGROUPID_SUPERUSER))
    {
      aUserGroupMgr.createPredefinedUserGroup (CAppSecurity.USERGROUPID_SUPERUSER, "Super users", null, null);
      // Assign administrator user to UG administrators
      aUserGroupMgr.assignUserToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CSecurity.USER_ADMINISTRATOR_ID);
    }
    aUserGroupMgr.assignRoleToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CAppSecurity.ROLEID_WRITE);
    aUserGroupMgr.assignRoleToUserGroup (CSecurity.USERGROUP_ADMINISTRATORS_ID, CAppSecurity.ROLEID_READ);

    // User group writing users
    if (!aUserGroupMgr.containsWithID (CAppSecurity.USERGROUPID_WRITER))
      aUserGroupMgr.createPredefinedUserGroup (CAppSecurity.USERGROUPID_WRITER, "Writing users", null, null);
    aUserGroupMgr.assignRoleToUserGroup (CAppSecurity.USERGROUPID_WRITER, CAppSecurity.ROLEID_WRITE);
    aUserGroupMgr.assignRoleToUserGroup (CAppSecurity.USERGROUPID_WRITER, CAppSecurity.ROLEID_READ);

    // User group reading users
    if (!aUserGroupMgr.containsWithID (CAppSecurity.USERGROUPID_READER))
      aUserGroupMgr.createPredefinedUserGroup (CAppSecurity.USERGROUPID_READER, "Reading users", null, null);
    aUserGroupMgr.assignRoleToUserGroup (CAppSecurity.USERGROUPID_READER, CAppSecurity.ROLEID_READ);
  }
}
