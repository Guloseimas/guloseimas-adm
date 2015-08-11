package bootstrap;

/**
 * Created by Alvaro on 29/03/2015.
 */
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import play.Application;
import play.Logger;
import play.Play;
import play.Plugin;

public class S3Plugin extends Plugin {

    public static final String AWS_S3_BUCKET = "aws.s3.bucket";
    public static final String AWS_ACCESS_KEY = "aws.access.key";
    public static final String AWS_SECRET_KEY = "aws.secret.key";
    private final Application application;

    public static AmazonS3 amazonS3;

    public static String s3Bucket;

    public S3Plugin(Application application) {
        this.application = application;
    }

    @Override
    public void onStart() {

        String accessKey = null;
        String secretKey = null;
        if(System.getenv("aws.access.key")!=null){
            accessKey = System.getenv("aws.access.key");
        }else{
            accessKey = Play.application().configuration().getString(AWS_ACCESS_KEY);
        }
        if(System.getenv("aws.secret.key")!=null){
            secretKey = System.getenv("aws.secret.key");
        }else{
            secretKey = Play.application().configuration().getString(AWS_SECRET_KEY);

        }
        if(System.getenv("aws.s3.bucket")!=null){
            s3Bucket = System.getenv("aws.s3.bucket");

        }else{
            s3Bucket = Play.application().configuration().getString(AWS_S3_BUCKET);

        }


        if ((accessKey != null) && (secretKey != null)) {
            AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
            amazonS3 = new AmazonS3Client(awsCredentials);
            try {
                amazonS3.createBucket(s3Bucket);
            }catch (Exception e){
                Logger.debug("criado");
            }
            Logger.info("Using S3 Bucket: " + s3Bucket);
        }
    }

    @Override
    public boolean enabled() {

         if(System.getenv("aws.access.key")!=null){
            return (System.getenv("aws.access.key")!=null &&
                System.getenv("aws.secret.key") !=null &&
                System.getenv("aws.s3.bucket")!=null );
        }else{
            return (application.configuration().keys().contains(AWS_ACCESS_KEY) &&
                application.configuration().keys().contains(AWS_SECRET_KEY) &&
                application.configuration().keys().contains(AWS_S3_BUCKET));
        }
    }


}