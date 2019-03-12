package com.helger.registry434.app;

import java.io.Serializable;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import com.helger.commons.collection.NonBlockingStack;
import com.helger.commons.io.resource.ClassPathResource;
import com.helger.xml.microdom.IMicroDocument;
import com.helger.xml.microdom.IMicroElement;
import com.helger.xml.microdom.serialize.MicroReader;

/**
 * Business term manager
 *
 * @author Philip Helger
 */
public final class BTManager
{
  public static abstract class AbstractBT implements Serializable
  {
    private final String m_sID;
    private final String m_sName;
    private final String m_sCard;

    public AbstractBT (final String sID, final String sName, final String sCard)
    {
      m_sID = sID;
      m_sName = sName;
      m_sCard = sCard;
    }

    public String getID ()
    {
      return m_sID;
    }

    public String getName ()
    {
      return m_sName;
    }

    public String getCard ()
    {
      return m_sCard;
    }
  }

  public static final class BusinessGroup extends AbstractBT
  {
    public BusinessGroup (final String sID, final String sName, final String sCard)
    {
      super (sID, sName, sCard);
    }
  }

  public static final class BusinessTerm extends AbstractBT
  {
    private final String m_sDataType;

    public BusinessTerm (final String sID, final String sName, final String sCard, final String sDataType)
    {
      super (sID, sName, sCard);
      m_sDataType = sDataType;
    }

    public String getDataType ()
    {
      return m_sDataType;
    }
  }

  private static final IMicroDocument BT_DOC = MicroReader.readMicroXML (new ClassPathResource ("codelist/bts.xml"));

  private static void _read (@Nonnull final IMicroElement aStart,
                             @Nonnull final NonBlockingStack <BusinessGroup> aStack,
                             @Nonnull final BiConsumer <NonBlockingStack <BusinessGroup>, AbstractBT> aConsumer)
  {
    for (final IMicroElement e : aStart.getAllChildElements ())
      if (e.getTagName ().equals ("business-group"))
      {
        final BusinessGroup aBG = new BusinessGroup (e.getAttributeValue ("id"),
                                                     e.getAttributeValue ("name"),
                                                     e.getAttributeValue ("card"));
        aConsumer.accept (aStack, aBG);
        aStack.push (aBG);
        _read (e, aStack, aConsumer);
        aStack.pop ();
      }
      else
        if (e.getTagName ().equals ("business-term"))
        {
          // Business term
          final BusinessTerm aBT = new BusinessTerm (e.getAttributeValue ("id"),
                                                     e.getAttributeValue ("name"),
                                                     e.getAttributeValue ("card"),
                                                     e.getAttributeValue ("dt"));
          aConsumer.accept (aStack, aBT);
        }
        else
          throw new IllegalStateException ("Unspported tag " + e.getTagName ());
  }

  private BTManager ()
  {}

  public static void foreach (@Nonnull final BiConsumer <NonBlockingStack <BusinessGroup>, AbstractBT> aConsumer)
  {
    final NonBlockingStack <BusinessGroup> aStack = new NonBlockingStack <> ();
    _read (BT_DOC.getDocumentElement (), aStack, aConsumer);
  }
}
