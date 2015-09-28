//
// Copyright (c) 2013, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   18 Apr 2013  Brian Frank  Creation
//
package org.projecthaystack.io;

import java.io.*;
import java.util.Iterator;
import java.util.Map.Entry;
import org.projecthaystack.*;

/**
 * HJsonWriter is used to write grids in JavaScript Object Notation.
 * It is a plain text format commonly used for serialization of data.
 ** It is specified in RFC 4627.
 *
 * @see <a href='http://project-haystack.org/doc/Json'>Project Haystack</a>
 */
public class HJsonWriter extends HGridWriter
{

//////////////////////////////////////////////////////////////////////////
// Construction
//////////////////////////////////////////////////////////////////////////

  /** Write using UTF-8 */
  public HJsonWriter(OutputStream out)
  {
    try
    {
      this.out = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }

  /** Write a grid to an in-memory a string */
  public static String gridToString(HGrid grid)
  {
    StringWriter out = new StringWriter(grid.numCols() * grid.numRows() * 32);
    new HJsonWriter(out).writeGrid(grid);
    return out.toString();
  }

  private HJsonWriter(StringWriter out) { this.out = new PrintWriter(out); }

//////////////////////////////////////////////////////////////////////////
// HGridWriter
//////////////////////////////////////////////////////////////////////////

  /** Write a grid */
  public void writeGrid(HGrid grid)
  {
    // grid begin
    out.print("{\n");

    // meta
    out.print("\"meta\": {\"ver\":\"2.0\"");
    writeDictTags(grid.meta(), false);
    out.print("},\n");

    // columns
    out.print("\"cols\":[\n");
    for (int i=0; i<grid.numCols(); ++i)
    {
      if (i > 0) out.print(",\n");
      HCol col = grid.col(i);
      out.print("{\"name\":");
      out.print(HStr.toCode(col.name()));
      writeDictTags(col.meta(), false);
      out.print("}");
    }
    out.print("\n],\n");

    // rows
    out.print("\"rows\":[\n");
    for (int i=0; i<grid.numRows(); ++i)
    {
      if (i > 0) out.print(",\n");
      writeDict(grid.row(i));
    }
    out.print("\n]\n");

    // grid end
    out.print("}\n");
    out.flush();
  }

  private void writeDict(HDict dict)
  {
    out.print("{");
    writeDictTags(dict, true);
    out.print("}");
  }

  private void writeDictTags(HDict dict, boolean first)
  {
    for (Iterator it = dict.iterator(); it.hasNext(); )
    {
      if (first) first = false; else out.print(", ");
      Entry entry = (Entry)it.next();
      String name = (String)entry.getKey();
      HVal val = (HVal)entry.getValue();
      out.print(HStr.toCode(name));
      out.print(":");
      writeVal(val);
    }
  }

  private void writeVal(HVal val)
  {
    if (val == null) out.print("null");
    else if (val instanceof HBool) out.print(val);
    else out.print(HStr.toCode(val.toJson()));
  }

  /** Flush underlying output stream */
  public void flush()
  {
    out.flush();
  }

  /** Close underlying output stream */
  public void close()
  {
    out.close();
  }

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

  private PrintWriter out;

}