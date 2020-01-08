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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.state.EChange;
import com.helger.dao.DAOException;
import com.helger.registry434.domain.CEDetailsList;
import com.helger.registry434.domain.CEHeaderManager;
import com.helger.registry434.domain.ICEHeader;

/**
 * Extension manager around {@link CEHeaderManager}.
 *
 * @author Philip Helger
 */
public class CEHeaderManagerExt extends CEHeaderManager
{
  public CEHeaderManagerExt (@Nullable final String sFilename) throws DAOException
  {
    super (sFilename);
  }

  @Nonnull
  public EChange setDetails (@Nonnull final ICEHeader aSelectedObject, @Nullable final CEDetailsList aDetailsList)
  {
    return updateCEHeader (aSelectedObject.getID (),
                           aSelectedObject.getName (),
                           aSelectedObject.getType (),
                           aSelectedObject.getCountry (),
                           aSelectedObject.getSector (),
                           aSelectedObject.getPurpose (),
                           aSelectedObject.getPublisher (),
                           aSelectedObject.getGovernor (),
                           aSelectedObject.getUnderlyingSpec (),
                           aSelectedObject.getFurtherInfo (),
                           aSelectedObject.externalURLs (),
                           aSelectedObject.getStatus (),
                           aSelectedObject.getContactName (),
                           aSelectedObject.getContactEmail (),
                           aDetailsList);
  }

}
