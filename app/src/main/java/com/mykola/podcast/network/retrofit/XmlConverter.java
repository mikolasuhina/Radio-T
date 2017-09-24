package com.mykola.podcast.network.retrofit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mykola.podcast.models.Podcast;
import com.mykola.podcast.utils.Parser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;


class XmlConverter extends Converter.Factory {

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new Converter<ResponseBody, List<Podcast>>() {
            @Override
            public List<Podcast> convert(final  ResponseBody value) throws IOException {

                String rss = value.string();

                try {
                    return Parser.parseRss(rss);
                } catch (ParserConfigurationException mE) {
                    mE.printStackTrace();
                } catch (SAXException mE) {
                    mE.printStackTrace();
                }

                return Collections.EMPTY_LIST;
            }
        };
    }
}
