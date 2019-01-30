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
import javax.annotation.Nullable;

import com.helger.commons.annotation.UsedViaReflection;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.exception.InitializationException;
import com.helger.scope.singleton.AbstractGlobalSingleton;
import com.helger.settings.ISettings;
import com.helger.settings.exchange.configfile.ConfigFile;
import com.helger.settings.exchange.configfile.ConfigFileBuilder;

/**
 * This class provides access to the settings as contained in the
 * <code>webapp.properties</code> file.
 *
 * @author Philip Helger
 */
public class AppSettings extends AbstractGlobalSingleton
{
  /** The name of the file containing the settings */
  public static final String FILENAME = "webapp.properties";
  private static final ConfigFile s_aCF;

  static
  {
    s_aCF = new ConfigFileBuilder ().addPath ("private-webapp.properties").addPath (FILENAME).build ();
    if (!s_aCF.isRead ())
      throw new InitializationException ("Failed to read config file");
  }

  @Deprecated
  @UsedViaReflection
  private AppSettings ()
  {}

  @Nonnull
  public static ISettings getSettingsObject ()
  {
    return s_aCF.getSettings ();
  }

  @Nullable
  public static String getGlobalDebug ()
  {
    return getSettingsObject ().getAsString ("global.debug");
  }

  @Nullable
  public static String getGlobalProduction ()
  {
    return getSettingsObject ().getAsString ("global.production");
  }

  @Nullable
  public static String getDataPath ()
  {
    return getSettingsObject ().getAsString ("webapp.datapath");
  }

  public static boolean isCheckFileAccess ()
  {
    return getSettingsObject ().getAsBoolean ("webapp.checkfileaccess", true);
  }

  public static boolean isTestVersion ()
  {
    return getSettingsObject ().getAsBoolean ("webapp.testversion", GlobalDebug.isDebugMode ());
  }
}
