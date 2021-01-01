/**
 * Copyright (C) 2019-2021 Philip Helger
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
package com.helger.registry434.servlet;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;

import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.state.EContinue;
import com.helger.photon.core.login.AbstractLoginManager;
import com.helger.photon.core.servlet.AbstractUnifiedResponseFilter;
import com.helger.photon.security.CSecurity;
import com.helger.photon.security.login.LoggedInUserManager;
import com.helger.registry434.ui.AppLoginManager;
import com.helger.servlet.response.UnifiedResponse;
import com.helger.web.scope.IRequestWebScopeWithoutResponse;

public final class AppSecureLoginFilter extends AbstractUnifiedResponseFilter
{
  private AbstractLoginManager m_aLogin;

  @Override
  public void init () throws ServletException
  {
    super.init ();
    // Make the application login configurable if you like
    m_aLogin = new AppLoginManager ();
  }

  @Override
  @Nonnull
  protected EContinue handleRequest (@Nonnull final IRequestWebScopeWithoutResponse aRequestScope,
                                     @Nonnull final UnifiedResponse aUnifiedResponse) throws ServletException
  {
    // FIXME always login default user
    if (GlobalDebug.isDebugMode () && false)
    {
      final LoggedInUserManager aLIUM = LoggedInUserManager.getInstance ();
      if (!aLIUM.isUserLoggedInInCurrentSession ())
      {
        aLIUM.loginUser (CSecurity.USER_ADMINISTRATOR_EMAIL, CSecurity.USER_ADMINISTRATOR_PASSWORD);
      }
    }

    if (m_aLogin.checkUserAndShowLogin (aRequestScope, aUnifiedResponse).isBreak ())
    {
      // Show login screen
      return EContinue.BREAK;
    }

    return EContinue.CONTINUE;
  }
}
