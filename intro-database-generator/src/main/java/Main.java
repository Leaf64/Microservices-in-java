import com.mongodb.MongoClient;
import dao.TagDao;
import dao.UserDao;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import util.TagGenerator;
import util.UserGenerator;

import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {

    static int amountOfUsers;
    static int maxTagsPerUser;


    public static void main(String[] args) throws Exception {
        Morphia morphia = createAndConfigureMorphia();
        Datastore datastore = createAndConfigureDataStore(morphia);

        UserDao userDao = new UserDao(datastore);
        TagDao tagDao = new TagDao(datastore);
        TagGenerator tagGen = new TagGenerator(tagDao);
        UserGenerator userGen = new UserGenerator(userDao, tagGen);
        Scanner scan = new Scanner(System.in);

        amountOfUsers = getAmountOfUsersFromUser(scan);
        maxTagsPerUser = getAmountOfTagsFromUser(scan);

        userGen.start(amountOfUsers,maxTagsPerUser);
        System.out.println("Finished succesfully");
    }

    private static Morphia createAndConfigureMorphia() {
        Morphia morphia = new Morphia();
        morphia.mapPackage("entity.UserTagEntity");
        return morphia;
    }

    private static Datastore createAndConfigureDataStore(Morphia morphia) throws UnknownHostException {
        Datastore datastore = morphia.createDatastore(new MongoClient(), "introdb");
        datastore.ensureIndexes();
        return datastore;
    }

    private static int getAmountOfUsersFromUser(Scanner scan) {
        while (true) {
            System.out.println("Input amount of users you want to create: ");
            amountOfUsers = scan.nextInt();

            if (amountOfUsers > 0 && amountOfUsers < 25000) {
                break;
            } else {
                System.out.println("Number invalid, input number between 0 and 25k");
            }
        }
        return amountOfUsers;
    }

    private static int getAmountOfTagsFromUser(Scanner scan) {
        while (true) {
            System.out.println("Input amount of maximum number of tags per user you want to create: ");
            maxTagsPerUser = scan.nextInt();

            if (maxTagsPerUser > 0 && maxTagsPerUser < 1000) {
                break;
            } else {
                System.out.println("Number invalid, input number between 0 and 1000");
            }
        }
        return maxTagsPerUser;
    }

}
