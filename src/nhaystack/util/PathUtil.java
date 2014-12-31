//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   31 Aug 2013  Mike Jarmy  Creation
//
package nhaystack.util;

public abstract class PathUtil
{
    /**
      * Convert an AX path, which may contain '/', or escape sequences like '$20' or '$7f',
      * into a haystack-friendly path encoding.
      *
      * This is done by replacing: 
      *     '/'   with '.'
      *     '$20' with '-'
      *     '$'   with '~'
      */
    public static String fromNiagaraPath(String path)
    {
        StringBuffer sb = new StringBuffer();
        int n = 0;
        while (n < path.length())
        {
            char ch = path.charAt(n);
            switch(ch)
            {
                case '/': 
                    sb.append('.'); 
                    n++;
                    break;

                case '$': 

                    if ((n < path.length()-2) &&
                        (path.charAt(n+1) == '2') &&
                        (path.charAt(n+2) == '0'))
                    {
                        sb.append('-'); 
                        n += 3;
                        break;
                    }
                    else
                    {
                        sb.append('~'); 
                        n++;
                        break;
                    }

                default: 
                    sb.append(ch);
                    n++;
            }
        }
        return sb.toString();
    }

    /**
      * Convert a haystack-friendly path encoding into 
      * an AX path, which may contain '/', or escape sequences like '$20' or '$7f'.
      *
      * This is done by replacing: 
      *     '.' with '/'
      *     '-' with '$20'
      *     '~' with '$'
      */
    public static String toNiagaraPath(String path)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < path.length(); i++)
        {
            char ch = path.charAt(i);
            switch(ch)
            {
                case '.': sb.append('/');   break;
                case '-': sb.append("$20"); break;
                case '~': sb.append('$');   break;
                default: sb.append(ch);
            }
        }
        return sb.toString();
    }
}
