//
// Copyright (c) 2012, J2 Innovations
// Licensed under the Academic Free License version 3.0
//
// History:
//   31 Aug 2013  Mike Jarmy  Creation
//
package nhaystack.util;

public abstract class SlotUtil
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
    public static String fromNiagara(String path)
    {
        StringBuilder sb = new StringBuilder();
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

                    if ((n < (path.length() - 2)) &&
                        (path.charAt(n + 1) == '2') &&
                        (path.charAt(n + 2) == '0'))
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
    public static String toNiagara(String path)
    {
        StringBuilder sb = new StringBuilder();
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

    public static String fromEnum(String value, boolean translate)
    {
        return translate ? fromNiagara(value) : value;
    }
}
