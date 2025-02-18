package com.cuzz.webac.bot.objects.info.ocr;

import java.util.List;

public class ImageOCR {

    protected List<TextDetection> texts;
    protected String language;

    public List<TextDetection> getTexts() {
        return texts;
    }

    public String getLanguage() {
        return language;
    }
}
