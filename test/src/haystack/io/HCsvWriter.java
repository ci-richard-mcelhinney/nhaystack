//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   29 Sep 2012  Brian Frank  Creation
//
package haystack.io;

import java.io.*;
import java.util.*;
import haystack.*;

/**
 * HCsvWriter is used to write grids in comma separated values
 * format as specified by RFC 4180.  Format details:
 * <ul>
 * <li>rows are delimited by a newline</li>
 * <li>cells are separated by configured delimiter char (default is comma)</li>
 * <li>cells containing the delimiter, '"' double quote, or
 *     newline are quoted; quotes are escaped as with two quotes</li>
 * </ul>
 *
 * @see <a href='http://project-haystack.org/doc/Csv'>Project Haystack</a>
 */
public class HCsvWriter extends HGridWriter
{

//////////////////////////////////////////////////////////////////////////
// Construction
//////////////////////////////////////////////////////////////////////////

  /** Write using UTF-8 */
  public HCsvWriter(OutputStream out)
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

//////////////////////////////////////////////////////////////////////////
// HGridWriter
//////////////////////////////////////////////////////////////////////////

  /** Write a grid */
  public void writeGrid(HGrid grid)
  {
    // cols
    for (int i=0; i<grid.numCols(); ++i)
    {
      if (i > 0) out.write((char)delimiter);
      writeCell(grid.col(i).dis());
    }
    out.write('\n');

    // rows
    for (int i=0; i<grid.numRows(); ++i)
    {
      writeRow(grid, grid.row(i));
      out.write('\n');
    }
  }

  private void writeRow(HGrid grid, HRow row)
  {
    for (int i=0; i<grid.numCols(); ++i)
    {
      HVal val = row.get(grid.col(i), false);
      if (i > 0) out.write((char)delimiter);
      writeCell(valToString(val));
    }
  }

  private String valToString(HVal val)
  {
    if (val == null) return "";

    if (val == HMarker.VAL) return "\u2713";

    if (val instanceof HRef)
    {
      HRef ref = (HRef)val;
      String s = "@" + ref.val;
      if (ref.dis != null) s += " " + ref.dis;
      return s;
    }

    return val.toString();
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
// CSV
//////////////////////////////////////////////////////////////////////////

  /** Write a cell */
  public void writeCell(String cell)
  {
    if (!isQuoteRequired(cell))
    {
      out.print(cell);
    }
    else
    {
      out.print('"');
      for (int i=0; i<cell.length(); ++i)
      {
        int c = cell.charAt(i);
        if (c == '"') out.print('"');
        out.print((char)c);
      }
      out.print('"');
    }
  }

  /**
   * Return if the given cell string contains:
   * <ul>
   * <li>the configured delimiter</li>
   * <li>double quote '"' char</li>
   * <li>leading/trailing whitespace</li>
   * <li>newlines</li>
   * </ul>
   */
  public boolean isQuoteRequired(String cell)
  {
    if (cell.length() == 0) return true;
    if (isWhiteSpace(cell.charAt(0))) return true;
    if (isWhiteSpace(cell.charAt(cell.length()-1))) return true;
    for (int i=0; i<cell.length(); ++i)
    {
      int c = cell.charAt(i);
      if (c == delimiter || c == '"' || c == '\n' || c == '\r')
        return true;
    }
    return false;
  }

  static boolean isWhiteSpace(int c)
  {
    return c == ' ' || c == '\t' || c == '\n' || c == '\r';
  }

//////////////////////////////////////////////////////////////////////////
// Fields
//////////////////////////////////////////////////////////////////////////

  /** Delimiter used to write each cell */
  public char delimiter = ',';

  private PrintWriter out;

}