package edu.byu.cs.tweeter.model.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Follow;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.service.response.FollowingResponse;

class ServerFacadeTest {

    private static final String MALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/donald_duck.png";
    private static final String FEMALE_IMAGE_URL = "https://faculty.cs.byu.edu/~jwilkerson/cs340/tweeter/images/daisy_duck.png";
    private static final String MIKE = "https://i.imgur.com/VZQQiQ1.jpg";

    private final User user1 = new User("Daffy", "Duck", "", "password");
    private final User user2 = new User("Fred", "Flintstone", "", "password");
    private final User user3 = new User("Barney", "Rubble", "", "password"); // 2 followees
    private final User user4 = new User("Wilma", "Rubble", "", "password");
    private final User user5 = new User("Clint", "Eastwood", "", "password"); // 6 followees
    private final User user6 = new User("Mother", "Teresa", "", "password"); // 7 followees
    private final User user7 = new User("Harriett", "Hansen", "", "password");
    private final User user8 = new User("Zoe", "Zabriski", "", "password");
    private final User user9 = new User("Albert", "Awesome", "", "password"); // 1  followee
    private final User user10 = new User("Star", "Student", "", "password");
    private final User user11 = new User("Bo", "Bungle", "", "password");
    private final User user12 = new User("Susie", "Sampson", "", "password");
    private final User BillBelichick = new User("Bill", "Belichick", "@BillBelichick", MIKE, "password");
    private final User Snowden = new User("The", "Snowden", "@Snowden", MIKE, "password");
    private final User Allen = new User("Allen", "Anderson", MALE_IMAGE_URL, "password");
    private final User Amy = new User("Amy", "Ames", FEMALE_IMAGE_URL, "password");
    private final User Bonnie = new User("Bonnie", "Beatty", FEMALE_IMAGE_URL, "password");
    private final User theMedia = new User("the", "Media", "@theMedia", MIKE, "password");
    private final User Chris = new User("Chris", "Colston", MALE_IMAGE_URL, "password");
    private final User Cindy = new User("Cindy", "Coats", FEMALE_IMAGE_URL, "password");
    private final User Dan = new User("Dan", "Donaldson", MALE_IMAGE_URL, "password");


    private final Follow follow1 = new Follow(user9, user5);

    private final Follow follow2 = new Follow(user3, user1);
    private final Follow follow3 = new Follow(user3, user8);

    private final Follow follow4 = new Follow(user5,  user9);
    private final Follow follow5 = new Follow(user5,  user11);
    private final Follow follow6 = new Follow(user5,  user1);
    private final Follow follow7 = new Follow(user5,  user2);
    private final Follow follow8 = new Follow(user5,  user4);
    private final Follow follow9 = new Follow(user5,  user8);

    private final Follow follow10 = new Follow(user6,  user3);
    private final Follow follow11 = new Follow(user6,  user5);
    private final Follow follow12 = new Follow(user6,  user1);
    private final Follow follow13 = new Follow(user6,  user7);
    private final Follow follow14 = new Follow(user6,  user10);
    private final Follow follow15 = new Follow(user6,  user12);
    private final Follow follow16 = new Follow(user6,  user4);


    private final List<Follow> follows = Arrays.asList(follow1, follow2, follow3, follow4, follow5, follow6,
            follow7, follow8, follow9, follow10, follow11, follow12, follow13, follow14, follow15,
            follow16);

    private ServerFacade serverFacadeSpy;

    @BeforeEach
    void setup() {
        serverFacadeSpy = Mockito.spy(new ServerFacade());

        FollowGenerator mockFollowGenerator = Mockito.mock(FollowGenerator.class);
        Mockito.when(mockFollowGenerator.generateUsersAndFollows(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), (FollowGenerator.Sort) Mockito.any())).thenReturn(follows);

        Mockito.when(serverFacadeSpy.getFollowGenerator()).thenReturn(mockFollowGenerator);
    }

    @Test
    void testGetFollowees_noFolloweesForUser() {

        FollowingRequest request = new FollowingRequest(user5, 10, null);
        FollowingResponse response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(0, response.getFollowees().size());
        Assertions.assertFalse(response.getHasMorePages());
    }

    @Test
    void testGetFollowees_oneFollowerForUser_limitGreaterThanUsers() {

        FollowingRequest request = new FollowingRequest(user9, 10, null);
        FollowingResponse response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(1, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(BillBelichick));
        Assertions.assertFalse(response.getHasMorePages());
    }

    @Test
    void testGetFollowees_twoFollowersForUser_limitEqualsUsers() {

        FollowingRequest request = new FollowingRequest(user3, 2, null);
        FollowingResponse response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(2, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(Allen));
        Assertions.assertTrue(response.getFollowees().contains(Amy));
        Assertions.assertTrue(response.getHasMorePages());
    }

    @Test
    void testGetFollowees_limitLessThanUsers_endsOnPageBoundary() {

        FollowingRequest request = new FollowingRequest(Snowden, 2, null);
        FollowingResponse response = serverFacadeSpy.getFollowees(request);

        // Verify first page
        Assertions.assertEquals(2, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(Allen));
        Assertions.assertTrue(response.getFollowees().contains(Amy));
        Assertions.assertTrue(response.getHasMorePages());

        // Get and verify second page
        request = new FollowingRequest(Snowden, 2, response.getFollowees().get(1));
        response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(2, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(theMedia));
        Assertions.assertTrue(response.getFollowees().contains(Bonnie));
        Assertions.assertTrue(response.getHasMorePages());

        // Get and verify third page
        request = new FollowingRequest(Snowden, 2, response.getFollowees().get(1));
        response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(2, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(Chris));
        Assertions.assertTrue(response.getFollowees().contains(Cindy));
        Assertions.assertTrue(response.getHasMorePages());
    }


    @Test
    void testGetFollowees_limitLessThanUsers_notEndsOnPageBoundary() {

        FollowingRequest request = new FollowingRequest(BillBelichick, 2, null);
        FollowingResponse response = serverFacadeSpy.getFollowees(request);

        // Verify first page
        Assertions.assertEquals(2, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(Amy));
        Assertions.assertTrue(response.getFollowees().contains(Allen));
        Assertions.assertTrue(response.getHasMorePages());

        // Get and verify second page
        request = new FollowingRequest(BillBelichick, 2, response.getFollowees().get(1));
        response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(2, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(theMedia));
        Assertions.assertTrue(response.getFollowees().contains(Bonnie));
        Assertions.assertTrue(response.getHasMorePages());

        // Get and verify third page
        request = new FollowingRequest(BillBelichick, 2, response.getFollowees().get(1));
        response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(2, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(Chris));
        Assertions.assertTrue(response.getFollowees().contains(Cindy));
        Assertions.assertTrue(response.getHasMorePages());

        // Get and verify fourth page
        request = new FollowingRequest(BillBelichick, 1, response.getFollowees().get(1));
        response = serverFacadeSpy.getFollowees(request);

        Assertions.assertEquals(1, response.getFollowees().size());
        Assertions.assertTrue(response.getFollowees().contains(Dan));
        Assertions.assertTrue(response.getHasMorePages());
    }
}
