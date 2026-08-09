package com.henrique.nookio_api.core.configs;

import com.henrique.nookio_api.core.audit_logs.annotation.AuditLog;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DocsController {

    @AuditLog(resource = "DOCUMENTATION", operation = "VISUALIZE")
    @GetMapping(value = "/docs", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String getScalarDocs() {
        return """
            <!doctype html>
            <html>
              <head>
                <title>Nookio API Reference</title>
                <meta charset="utf-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1" />
                <style>
                  body {
                    margin: 0;
                    padding: 0;
                  }
                </style>
              </head>
              <body>
                <script
                  id="api-reference"
                  data-url="/v3/api-docs"></script>
                <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
              </body>
            </html>
            """;
    }
}
