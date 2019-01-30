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

import javax.annotation.concurrent.Immutable;

import com.helger.photon.security.CSecurity;

@Immutable
public final class CAppSecurity
{
  // Security role IDs
  public static final String ROLEID_WRITE = "write";
  public static final String ROLEID_READ = "read";

  // User group IDs
  public static final String USERGROUPID_SUPERUSER = CSecurity.USERGROUP_ADMINISTRATORS_ID;
  public static final String USERGROUPID_WRITER = "ug-writer";
  public static final String USERGROUPID_READER = "ug-reader";

  private CAppSecurity ()
  {}
}
