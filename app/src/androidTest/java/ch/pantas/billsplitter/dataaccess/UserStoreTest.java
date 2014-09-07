package ch.pantas.billsplitter.dataaccess;

import com.google.inject.Inject;

import org.mockito.Mock;

import ch.pantas.billsplitter.dataaccess.rowmapper.UserRowMapper;
import ch.pantas.billsplitter.model.User;

public class UserStoreTest extends BaseStoreTest {

    @Inject
    private UserStore store;

    @Mock
    private User user;

    @Mock
    private UserRowMapper mapper;




}
