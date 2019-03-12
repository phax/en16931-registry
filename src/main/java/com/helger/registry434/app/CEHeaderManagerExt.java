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
  public EChange setDetails (@Nonnull final ICEHeader aSelectedObject, @Nonnull final CEDetailsList aDetailsList)
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
