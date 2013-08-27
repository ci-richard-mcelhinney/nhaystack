//
// Copyright (c) 2012, Brian Frank
// Licensed under the Academic Free License version 3.0
//
// History:
//   24 Sep 2012  Brian Frank  Creation
//
package haystack.io;

import org.projecthaystack.*;

/**
 * HGridReader is base class for reading grids from an input stream.
 *
 * @see <a href='http://project-haystack.org/doc/Rest#contentNegotiation'>Project Haystack</a>
 */
public abstract class HGridReader
{

  /** Read a grid */
  public abstract HGrid readGrid();

}