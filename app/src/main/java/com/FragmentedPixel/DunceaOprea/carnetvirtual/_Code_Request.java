package com.FragmentedPixel.DunceaOprea.carnetvirtual;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vlad_ on 28.01.2017.
 */

class _Code_Request extends StringRequest {
    private static final String Site_URL_Login = "http://carnet-virtual.victoriacentre.ro/code_request_10.php";
    private Map<String, String> params;

    _Code_Request(String Version_Code,String Code, Response.Listener<String> listener) {
        super(Method.POST, Site_URL_Login, listener, null);
        params = new HashMap<>();
        params.put("VersionCode",Version_Code);
        params.put("AccessCode","696969");
        params.put("Code", Code);

    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}