package models;

import bootstrap.S3Plugin;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import play.Logger;
import services.MongoService;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Alvaro on 29/03/2015.
 */
@Document
    public class Estrutura {
        private String estrutura;
        private String color;

        public Estrutura(){

        }
        public String getEstrutura() {
            return estrutura;
        }

        public void setEstrutura(String estrutura) {
            this.estrutura = estrutura;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public int hashCode()
        {
            return (estrutura+color).hashCode();
        }

        public boolean equals(Object obj)
        {
            return (((Estrutura)this).getEstrutura().equals(((Estrutura)obj).getEstrutura())&&
                    ((Estrutura)this).getColor().equals(((Estrutura)obj).getColor()));
        }

        public String toString(){
            return "<p>"+this.estrutura + " <span style=\"background-color:"+this.color+"\"> cor </span> </p>" ;
        }

    }