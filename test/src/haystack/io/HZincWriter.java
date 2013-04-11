//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   25 Sep 2012  Brian Frank  Creation
//
package haystack.io;

import java.io.*;
import java.util.*;
import haystack.*;

/**
 * HZincWriter is used to write grids in the Zinc format
 *
 * @see <a href='http://project-haystack.org/doc/Zinc'>Project Haystack</a>
 */
public class HZincWriter extends HGridWriter
{

//////////////////////////////////////////////////////////////////////////
// Construction
//////////////////////////////////////////////////////////////////////////

  /** Write using UTF-8 */
  public HZincWriter(OutputStream out)
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
    StringWriter out = new StringWriter(grid.numCols() * grid.numRows() * 16);
    new HZincWriter(out).writeGrid(grid);
    return out.toString();
  }

  private HZincWriter(StringWriter out) { this.out = new PrintWriter(out); }

//////////////////////////////////////////////////////////////////////////
// HGridWriter
//////////////////////////////////////////////////////////////////////////

  /** Write a grid */
  public void writeGrid(HGrid grid)
  {
    // meta
    out.write("ver:\"2.0\"");
    writeMeta(grid.meta());
    out.write('\n');

    // cols
    for (int i=0; i<grid.numCols(); ++i)
    {
      if (i > 0) out.write(',');
      writeCol(grid.col(i));
    }
    out.write('\n');

    // rows
    for (int i=0; i<grid.numRows(); ++i)
    {
      writeRow(grid, grid.row(i));
      out.write('\n');
    }
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
// Implementation
//////////////////////////////////////////////////////////////////////////

  private void writeMeta(HDict meta)
  {
    if (meta.isEmpty()) return;
    for (Iterator it = meta.iterator(); it.hasNext(); )
    {
      Map.Entry entry = (Map.Entry)it.next();
      String name = (String)entry.getKey();
      HVal val = (HVal)entry.getValue();
      out.write(' ');
      out.write(name);
      if (val != HMarker.VAL)
      {
        out.write(':');
        out.write(val.toZinc());
      }
    }
  }

  private void writeCol(HCol col)
  {
    out.write(col.name());
    writeMeta(col.meta());
  }

  private void writeRow(HGrid grid, HRow row)
  {
    for (int i=0; i<grid.numCols(); ++i)
    {
      HVal val = row.get(grid.col(i), false);
      if (i > 0) out.write(',');
      if (val == null)
      {
        if (i == 0) out.write('N');
      }
      else
      {
        out.write(val.toZinc());
      }
    }
  }

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

  private PrintWriter out;

}