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
    public class EstruturaPreco {
        private String name;
        private  double price;

        public EstruturaPreco(){

        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int hashCode()
        {
            return (name+price).hashCode();
        }

        public boolean equals(Object obj)
        {
            return (((EstruturaPreco)this).getName().equals(((EstruturaPreco)obj).getName())&&
                    ((EstruturaPreco)this).getPrice()==((EstruturaPreco)obj).getPrice());
        }

    }