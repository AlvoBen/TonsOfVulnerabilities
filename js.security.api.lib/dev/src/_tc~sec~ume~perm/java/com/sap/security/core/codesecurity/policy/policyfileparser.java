package com.sap.security.core.codesecurity.policy;

import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;

public class PolicyFileParser extends PersistenceAdapter
{
    // read policy file through StreamTokenizer
    private StreamTokenizer mm_tokenizer;

    // stream tokenizer's token type field
    private int mm_lookahead;

    // file to read policy from
    private String policyfile = null;

    public PolicyFileParser()
    {
        super();
    }

    public PolicyFileParser(boolean flag)
    {
        super(flag);
    }

    public PolicyFileParser( String policyfile, boolean replaceprops )
        throws PolicyParserException
    {
        if( policyfile == null )
            throw new PolicyParserException("policyfile cannot be null");

        this.policyfile = policyfile;
    }

    public PolicyFileParser( String policyfile )
        throws PolicyParserException
    {
        this( policyfile, false );
    }



    public void initAdapterImpl() throws PersistenceAdapterException
    {
        boolean throwException = false;
        try
        {
            if( this.policyfile == null )
            {
                throwException = true;
                this.policyfile = getPropertyPrivileged("java.security.policy", "/");
                if( this.policyfile.startsWith("=") )
                    this.policyfile = this.policyfile.substring(1);
            }
            InputStreamReader r = new InputStreamReader(new FileInputStream(this.policyfile.replace('/',File.separatorChar)));
            parse(r);
            r.close();
        }
        catch( IOException ioe )
        {
            if( throwException )
                throw new PolicyParserException("could not read policy file: " + ioe.getMessage());
        }
    }

    /**
     * since the upload method does not really make sense for a file persistence,
     * just write the content to System.out
     * @param policyName ignored
     */
    protected void upload( String policyName )
    {
        PrintWriter wr = new PrintWriter(System.out);
        print(wr);
    }

    public void parse(Reader reader)
        throws PolicyParserException, IOException
    {
        // use a buffered reader (see javadoc of BufferedReader)
        if(!(reader instanceof BufferedReader))
            reader = new BufferedReader(reader);

        mm_tokenizer = new StreamTokenizer(reader);
        mm_tokenizer.resetSyntax();
        // what words can be built from
        mm_tokenizer.wordChars('a', 'z');
        mm_tokenizer.wordChars('A', 'Z');
        mm_tokenizer.wordChars('.', '.');
        mm_tokenizer.wordChars('0', '9');
        mm_tokenizer.wordChars('_', '_');
        mm_tokenizer.wordChars('$', '$');
        mm_tokenizer.wordChars(160, 255);
        // define whitespace
        mm_tokenizer.whitespaceChars(0, 32);
        /*
        we don't need this since slashSlaComments is true!
        // start char of single-line comment
        mm_tokenizer.commentChar('/');
        mm_tokenizer.ordinaryChar('/');
        */
        // string constant delimiters
        mm_tokenizer.quoteChar('\'');
        mm_tokenizer.quoteChar('\"');
        // leave capital chars in string constants
        mm_tokenizer.lowerCaseMode(false);
        // recognize comments
        mm_tokenizer.slashSlashComments(true);
        mm_tokenizer.slashStarComments(true);

        // now we get the tokens until we reach EOF
        for(mm_lookahead = mm_tokenizer.nextToken(); mm_lookahead != StreamTokenizer.TT_EOF;)
        {
            if(checkToken("grant"))
            {
                ProtectionDomainEntry pdEntry = parseProtectionDomainEntry();
                if(pdEntry != null)
                    add(pdEntry);
            }
            else
            {
            if(checkToken("keystore") && mm_keystore.isEmpty())
                mm_keystore = parseKeyStoreEntry();
            }
            readToken(";");
        }
    }

    private KeyStoreEntry parseKeyStoreEntry()
        throws PolicyParserException, IOException
    {
        KeyStoreEntry kse = new KeyStoreEntry(mm_replaceProp);
        readToken("keystore");
        kse.mm_keyStoreUrlString = readToken("quoted string");
        if(!checkToken(","))
            return kse;
        readToken(",");
        if(checkToken("\""))
            kse.mm_keyStoreType = readToken("quoted string");
        else
            throw new PolicyParserException(mm_tokenizer.lineno(), "no keystore type found");

        return kse;
    }

    private ProtectionDomainEntry parseProtectionDomainEntry()
        throws PolicyParserException, IOException
    {
        ProtectionDomainEntry pdentry = new ProtectionDomainEntry();
        // read away the "grant"
        readToken("grant");
        // codebase or signedby of both can be before '{'
        while(!checkToken("{"))
        {
            if(checkAndReadToken("codebase"))
            {
                pdentry.mm_codeBase = readToken("quoted string");
                checkAndReadToken(",");
            }
            else if(checkAndReadToken("SignedBy"))
            {
                pdentry.mm_signedBy = readToken("quoted string");
                checkAndReadToken(",");
            }
            else
            {
                throw new PolicyParserException(mm_tokenizer.lineno(), "expected codeBase or SignedBy");
            }
        }
        // read away the '{'
        readToken("{");
        // before next '}', there can be only permission entries
        while(!checkToken("}"))
        {
            if(checkToken("permission"))
            {
                try
                {
                    PermissionEntry permissionentry = parsePermissionEntry();
                    pdentry.add(permissionentry);
                }
                catch(PropertyReplacer.PropertyReplacerException re)
                {
                    // if system property replacing fails for this permission entry, we ignore it
                    skipEntry();
                }
                // permissions are separated by ';'
                readToken(";");
            }
            else
            {
                throw new PolicyParserException(mm_tokenizer.lineno(), "expected permission entry");
            }
        }
        // read away the '}'
        readToken("}");

        try
        {
            if(pdentry.mm_codeBase != null)
                pdentry.mm_codeBase = replaceProps(pdentry.mm_codeBase).replace(File.separatorChar, '/');
            pdentry.mm_signedBy = replaceProps(pdentry.mm_signedBy);
        }
        catch(PropertyReplacer.PropertyReplacerException re)
        {
            return null;
        }
        return pdentry;
    }

    private PermissionEntry parsePermissionEntry()
        throws PolicyParserException, IOException, PropertyReplacer.PropertyReplacerException
    {
        PermissionEntry permissionentry = new PermissionEntry();
        // read away the permission keyword
        readToken("permission");
        // read the classname of the permission, e.g.
        // "java.awt.AWTPermission"
        permissionentry.mm_permissiontype = readToken("permission type");
        // read the name of the permission, e.g.
        // "accessClipBoard" (if a string is coming)
        if(checkToken("\""))
            permissionentry.mm_name = replaceProps(readToken("quoted string"));
        // no other string separated by ',' coming, this permnission entry is done
        if(!checkToken(","))
            return permissionentry;
        // okay, there was a ',', the action string is coming
        readToken(",");
        if(checkToken("\""))
            permissionentry.mm_action = replaceProps(readToken("quoted string"));

        return permissionentry;
    }

    private boolean checkAndReadToken(String tok)
        throws PolicyParserException, IOException
    {
        if(checkToken(tok))
        {
            readToken(tok);
            return true;
        } else
        {
            return false;
        }
    }

    /**
     * returns true, if the current token in the tokenizer equals the string s,
     * otherwise false.
     */
    private boolean checkToken(String tok)
    {
        boolean flag = false;
        switch(mm_lookahead)
        {
        default:
            break;

        case StreamTokenizer.TT_WORD:
            if(tok.equalsIgnoreCase(mm_tokenizer.sval))
                // it's the word we expected it to be
                flag = true;
            break;

        /**
         * now come the characters, which are tokens themselves
         */
        case ',':
            if(tok.equalsIgnoreCase(","))
                flag = true;
            break;

        case '{':
            if(tok.equalsIgnoreCase("{"))
                flag = true;
            break;

        case '}':
            if(tok.equalsIgnoreCase("}"))
                flag = true;
            break;

        case '\"':
            if(tok.equalsIgnoreCase("\""))
                flag = true;
            break;
        }
        return flag;
    }

    /**
     * reads one token, returns value of current token for special tokens:
     * * permission type
     * * quoted string
     */
    private String readToken(String tok)
        throws PolicyParserException, IOException
    {
        String sval = null;
        switch(mm_lookahead)
        {
        case StreamTokenizer.TT_NUMBER:
            throw new PolicyParserException(mm_tokenizer.lineno(), tok, "number " + String.valueOf(mm_tokenizer.nval));

        case StreamTokenizer.TT_EOF:
            throw new PolicyParserException("expected " + tok + ", read end of file");

        case StreamTokenizer.TT_WORD:
            if(tok.equalsIgnoreCase(mm_tokenizer.sval))
            {
                mm_lookahead = mm_tokenizer.nextToken();
                break;
            }
            if(tok.equalsIgnoreCase("permission type"))
            {
                sval = mm_tokenizer.sval;
                mm_lookahead = mm_tokenizer.nextToken();
            }
            else
            {
                throw new PolicyParserException(mm_tokenizer.lineno(), tok, mm_tokenizer.sval);
            }
            break;

        case '\"':
            if(tok.equalsIgnoreCase("quoted string"))
            {
                sval = mm_tokenizer.sval;
                mm_lookahead = mm_tokenizer.nextToken();
                break;
            }
            if(tok.equalsIgnoreCase("permission type"))
            {
                sval = mm_tokenizer.sval;
                mm_lookahead = mm_tokenizer.nextToken();
            }
            else
            {
                throw new PolicyParserException(mm_tokenizer.lineno(), tok, mm_tokenizer.sval);
            }
            break;

        case ',':
            if(tok.equalsIgnoreCase(","))
                mm_lookahead = mm_tokenizer.nextToken();
            else
                throw new PolicyParserException(mm_tokenizer.lineno(), tok, ",");
            break;

        case '{':
            if(tok.equalsIgnoreCase("{"))
                mm_lookahead = mm_tokenizer.nextToken();
            else
                throw new PolicyParserException(mm_tokenizer.lineno(), tok, "{");
            break;

        case '}':
            if(tok.equalsIgnoreCase("}"))
                mm_lookahead = mm_tokenizer.nextToken();
            else
                throw new PolicyParserException(mm_tokenizer.lineno(), tok, "}");
            break;

        case ';':
            if(tok.equalsIgnoreCase(";"))
                mm_lookahead = mm_tokenizer.nextToken();
            else
                throw new PolicyParserException(mm_tokenizer.lineno(), tok, ";");
            break;

        default:
            throw new PolicyParserException(mm_tokenizer.lineno(), tok, new String(new char[] {
                (char)mm_lookahead
            }));
        }
        return sval;
    }

    private void skipEntry()
        throws PolicyParserException, IOException
    {
        while(mm_lookahead != ';')
        {
            switch(mm_lookahead)
            {
            case StreamTokenizer.TT_NUMBER:
                throw new PolicyParserException(mm_tokenizer.lineno(), ";", "number " + String.valueOf(mm_tokenizer.nval));

            case StreamTokenizer.TT_EOF:
                throw new PolicyParserException("expected ';', found end of file");

            default:
                mm_lookahead = mm_tokenizer.nextToken();
                break;
            }
        }
    }
}
