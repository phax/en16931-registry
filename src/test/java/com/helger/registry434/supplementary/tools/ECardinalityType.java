package com.helger.registry434.supplementary.tools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

/**
 * This class was initially automatically created
 *
 * @author JDMCodeGenerator
 */
public enum ECardinalityType implements IHasID <String>
{
  _01 ("0..1"),
  _0N ("0..n"),
  _11 ("1..1"),
  _1n ("1..n");
  private final String m_sID;

  private ECardinalityType (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  @Nullable
  public static ECardinalityType getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (ECardinalityType.class, sID);
  }
}
