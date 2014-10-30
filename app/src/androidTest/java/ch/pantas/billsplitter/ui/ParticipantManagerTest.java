package ch.pantas.billsplitter.ui;

import android.test.suitebuilder.annotation.SmallTest;

import com.google.inject.Inject;

import java.util.List;

import ch.pantas.billsplitter.framework.BaseMockitoInstrumentationTest;
import ch.pantas.billsplitter.model.User;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;

public class ParticipantManagerTest extends BaseMockitoInstrumentationTest {

    @Inject
    private ParticipantManager participantManager;

    private User user;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        user = new User(randomUUID().toString());
    }

    @SmallTest
    public void testAddParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        try {
            participantManager.addParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testAddParticipantAddsParticipantCorrectly() {
        // When
        participantManager.addParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(1, participants.size());
        assertEquals(user, participants.get(0));
    }

    @SmallTest
    public void testAddingAlreadyExistingParticipantDoesNotAddHimTwice() {
        // Given
        participantManager.addParticipant(user);

        // When
        participantManager.addFixedParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(1, participants.size());
        assertEquals(user, participants.get(0));
    }

    @SmallTest
    public void testAddingAlreadyFixedParticipantDoesNotAddHimTwice() {
        // Given
        participantManager.addFixedParticipant(user);

        // When
        participantManager.addFixedParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(1, participants.size());
        assertEquals(user, participants.get(0));
    }

    @SmallTest
    public void testRemoveParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        try {
            participantManager.removeParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testRemoveParticipantRemovesParticipantCorrectly() {
        // Given
        participantManager.addParticipant(user);

        // When
        participantManager.removeParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(0, participants.size());
    }

    @SmallTest
    public void testRemoveParticipantDoesNotRemoveParticipantIfItIsFixed() {
        // Given
        participantManager.addFixedParticipant(user);

        // When
        participantManager.removeParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(1, participants.size());
        assertEquals(user, participants.get(0));
    }

    @SmallTest
    public void testAddFixedParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        try {
            participantManager.addFixedParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testAddFixedParticipantAddsParticipantCorrectly() {
        // When
        participantManager.addFixedParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(1, participants.size());
        assertEquals(user, participants.get(0));
    }

    @SmallTest
    public void testAddFixedParticipantDoesNotAddParticipantIfItIsAlreadyFixed() {
        // Given
        participantManager.addFixedParticipant(user);

        // When
        participantManager.addFixedParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(1, participants.size());
        assertEquals(user, participants.get(0));
    }

    @SmallTest
    public void testAddFixedParticipantMakesAlreadyAddedParticipantFixed() {
        // Given
        participantManager.addParticipant(user);

        // When
        participantManager.addFixedParticipant(user);

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(1, participants.size());
        assertEquals(user, participants.get(0));
        assertTrue(participantManager.isFixedParticipant(user));
    }

    @SmallTest
    public void testIsFixedParticipantThrowsNullPointerExceptionIfNoUserProvided() {
        try {
            participantManager.isFixedParticipant(null);
            fail("No exception has been thrown");
        } catch (NullPointerException e) {
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testIsFixedParticipantReturnsTrueIfParticipantIsFixedParticipant() {
        // Given
        participantManager.addFixedParticipant(user);

        // When
        boolean result = participantManager.isFixedParticipant(user);

        // Then
        assertTrue(result);
    }

    @SmallTest
    public void testIsFixedParticipantReturnsFalseIfParticipantIsNotFixed() {
        // Given
        participantManager.addParticipant(user);

        // When
        boolean result = participantManager.isFixedParticipant(user);

        // Then
        assertFalse(result);
    }

    @SmallTest
    public void testIsFixedParticipantReturnsFalseIfParticipantDoesNotExist() {
        // When
        boolean result = participantManager.isFixedParticipant(user);

        // Then
        assertFalse(result);
    }

    @SmallTest
    public void testGetParticipantsReturnsEmptyListIfNoParticipantExists() {
        // When
        List<User> participants = participantManager.getParticipants();

        // Then
        assertNotNull(participants);
        assertEquals(0, participants.size());
    }

    @SmallTest
    public void testGetParticipantsReturnsFixedParticipantAndNonFixedParticipant() {
        // Given
        User user2 = new User(randomUUID().toString(), "Mary");
        participantManager.addFixedParticipant(user);
        participantManager.addParticipant(user2);

        // When
        List<User> participants = participantManager.getParticipants();

        // Then
        assertNotNull(participants);
        assertEquals(2, participants.size());
        assertEquals(user, participants.get(0));
        assertEquals(user2, participants.get(1));
    }

    @SmallTest
    public void testFilterOutParticipantsThrowsNullPointerExceptionIfNoInputProvided(){
        try {
            participantManager.filterOutParticipants(null);
            fail("No exception has been thrown.");
        } catch(NullPointerException e){
            assertNotNull(e);
        }
    }

    @SmallTest
    public void testFilterOutParticipantsDoesNotFilterOutNonParticipant(){
        // Given
        List<User> users = asList(user);

        // When
        List<User> result = participantManager.filterOutParticipants(users);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(user, result.get(0));
    }

    @SmallTest
    public void testFilterOutParticipantsFiltersOutFixedParticipant(){
        // Given
        participantManager.addFixedParticipant(user);
        List<User> users = asList(user);

        // When
        List<User> result = participantManager.filterOutParticipants(users);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @SmallTest
    public void testFilterOutParticipantsFiltersOutNonFixedParticipant(){
        // Given
        participantManager.addParticipant(user);
        List<User> users = asList(user);

        // When
        List<User> result = participantManager.filterOutParticipants(users);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @SmallTest
    public void testClearClearsFixedAndNonFixedParticipants(){
        // Given
        participantManager.addFixedParticipant(user);
        participantManager.addParticipant(user);

        // When
        participantManager.clear();

        // Then
        List<User> participants = participantManager.getParticipants();
        assertEquals(0, participants.size());
    }
}
