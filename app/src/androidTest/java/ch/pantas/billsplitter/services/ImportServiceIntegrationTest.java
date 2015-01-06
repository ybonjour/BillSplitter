package ch.pantas.billsplitter.services;

import android.test.suitebuilder.annotation.SmallTest;

import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import ch.pantas.billsplitter.model.Participant;
import ch.pantas.billsplitter.model.User;

import static java.util.UUID.randomUUID;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImportServiceIntegrationTest extends ImportServiceBaseTest {

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        when(sharedPreferenceService.getUserId()).thenReturn(me.getId());

    }

    @SmallTest
    public void testImportEventSameParticipantWithOtherUser() {
        // Given
        final String username = "Joe";
        User user = new User(randomUUID(), username);
        when(userStore.getUserWithName(user.getName())).thenReturn(user);
        when(userStore.getById(user.getId())).thenReturn(user);

        User newUser = new User(randomUUID(), username);
        Participant participant = addParticipant(eventDto, newUser);
        participant.setUserId(user.getId());
        when(participantStore.getById(participant.getId())).thenReturn(participant);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                when(userStore.getUserWithName(username)).thenReturn(null);
                return null;
            }
        }).when(userStore).removeById(user.getId());

        // When
        importService.importEvent(eventDtoOperator);

        // Then
        verify(userStore, times(1)).createExistingModel(newUser);
        assertEquals(username, newUser.getName());
    }


}
