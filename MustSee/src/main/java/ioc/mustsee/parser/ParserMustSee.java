package ioc.mustsee.parser;

import java.util.HashMap;
import java.util.Map;

import ioc.mustsee.data.Categoria;
import ioc.mustsee.data.Comentari;
import ioc.mustsee.data.Lloc;
import ioc.mustsee.downloaders.DownloadXMLAsyncTaskGET;
import ioc.mustsee.downloaders.DownloadXMLAsyncTaskPOST;
import ioc.mustsee.downloaders.OnTaskCompleted;

public class ParserMustSee {
    private static final String TAG = "ParserMustSee";
    private static final String URL_LLOCS = "http://mustseers.hol.es/api/v1/llocs.xml";
    private static final String URL_CATEGORIES = "http://mustseers.hol.es/api/v1/categories.xml";
    private static final String URL_AUTH = "http://mustseers.hol.es/api/v1/auth.xml";
    private static final String URL_COMMENT_FROM_LLOC = "http://mustseers.hol.es/api/v1/comentaris/llocs/";

    public void getLlocs(OnTaskCompleted callback) {
        new DownloadXMLAsyncTaskGET<Lloc>(callback, "llocs").execute(URL_LLOCS);
    }

    public void getCategories(OnTaskCompleted callback) {
        new DownloadXMLAsyncTaskGET<Categoria>(callback, "categories").execute(URL_CATEGORIES);
    }

    public void getAuth(OnTaskCompleted callback, String correu, String password) throws RuntimeException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("correu", correu);
        params.put("password", password);

        new DownloadXMLAsyncTaskGET<Boolean>(callback, "auth", params).execute(URL_AUTH);
    }

    public void postComment(OnTaskCompleted callback, String correu, String password, String text, int llocId) {
        String url = URL_COMMENT_FROM_LLOC + llocId + ".xml";

        Map<String, String> params = new HashMap<String, String>();
        params.put("correu", correu);
        params.put("password", password);
        params.put("comentari", text);

        new DownloadXMLAsyncTaskPOST<Boolean>(callback, "comentari", params).execute(url);
    }

    public void getComentarisFromLloc(OnTaskCompleted callback, int llocId) {
        String url = URL_COMMENT_FROM_LLOC + llocId + ".xml";
        new DownloadXMLAsyncTaskGET<Comentari>(callback, "comentaris").execute(url);
    }
}
