//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   25 Sep 2012  Brian Frank  Creation
//
package haystack.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * HGridFormat models a format used to encode/decode HGrid.
 *
 * @see <a href='http://project-haystack.org/doc/Rest#contentNegotiation'>Project Haystack</a>
 */
public class HGridFormat
{
  /**
   * Find the HGridFormat for the given mime type.  The mime type
   * may contain parameters in which case they are automatically stripped
   * for lookup.  Throw a RuntimeException or return null based on
   * checked flag if the mime type is not registered to a format.
   */
  public static HGridFormat find(String mime, boolean checked)
  {
    // normalize mime type to strip parameters
    int semicolon = mime.indexOf(';');
    if (semicolon > 0) mime = mime.substring(0, semicolon).trim();

    // lookup format
    HGridFormat format = null;
    synchronized (registry)
    {
      format = (HGridFormat)registry.get(mime);
    }
    if (format != null) return format;

    // handle missing
    if (checked) throw new RuntimeException("No format for mime type: " + mime);
    return null;
  }

  /**
   * List all registered formats
   */
  public static HGridFormat[] list()
  {
    synchronized (registry)
    {
      HGridFormat[] acc = new HGridFormat[registry.size()];
      Iterator it = registry.values().iterator();
      for (int i=0; it.hasNext(); ++i) acc[i] = (HGridFormat)it.next();
      return acc;
    }
  }

  /**
   * Register a new HGridFormat
   */
  public static void register(HGridFormat format)
  {
    synchronized (registry)
    {
      registry.put(format.mime, format);
    }
  }

  /** Constructor */
  public HGridFormat(String mime, Class reader, Class writer)
  {
    if (mime.indexOf(';') >= 0)
      throw new IllegalArgumentException("mime has semicolon " + mime);
    this.mime = mime;
    this.reader = reader;
    this.writer = writer;
  }

  /**
   * Mime type for the format with no paramters, such as "text/zinc".
   * All text formats are assumed to be utf-8.
   */
  public final String mime;

  /**
   * Class of HGridReader used to read this format
   * or null if reading is unavailable.
   */
  public final Class reader;

  /**
   * Class of HGridWriter used to write this format
   * or null if writing is unavailable.
   */
  public final Class writer;

  /**
   * Make instance of "reader"; constructor with InputStream is expected.
   */
  public HGridReader makeReader(InputStream in)
  {
    if (reader == null) throw new RuntimeException("Format doesn't support reader: " + mime);
    try
    {
      return (HGridReader)reader
        .getConstructor(new Class[] { InputStream.class })
        .newInstance(new Object[] { in });
    }
    catch (Throwable e)
    {
      throw new RuntimeException("Cannot construct: " + reader.getName() + "(InputStream)", e);
    }
  }

  /**
   * Make instance of "writer"; constructor with OutputStream is expected.
   */
  public HGridWriter makeWriter(OutputStream out)
  {
    if (writer == null) throw new RuntimeException("Format doesn't support writer: " + mime);
    try
    {
      return (HGridWriter)writer
        .getConstructor(new Class[] { OutputStream.class })
        .newInstance(new Object[] { out });
    }
    catch (Throwable e)
    {
      throw new RuntimeException("Cannot construct: " + writer.getName() + "(OutputStream)", e);
    }
  }

  private static HashMap registry = new HashMap();
  static
  {
    try
    {
      register(new HGridFormat("text/plain", HZincReader.class, HZincWriter.class));
      register(new HGridFormat("text/zinc",  HZincReader.class, HZincWriter.class));
      register(new HGridFormat("text/csv",   null,              HCsvWriter.class));
    }
    catch (Throwable e) { e.printStackTrace(); }
  }

}