import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.dto.CardRequest;
import ru.dto.CardResponse;
import ru.encryptionUtil.EncryptionUtil;
import ru.exception.AccessDeniedException;
import ru.mapper.CardResponseMapper;
import ru.model.Card;
import ru.model.CardStatus;
import ru.model.Role;
import ru.model.User;
import ru.repository.CardRepository;
import ru.repository.UserRepository;
import ru.service.CardServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock private CardRepository cardRepository;
    @Mock private UserRepository userRepository;
    @Mock private EncryptionUtil encryptionUtil;
    @Mock private CardResponseMapper cardResponseMapper;

    @InjectMocks private CardServiceImpl cardService;

    private User testUser;
    private Card testCard;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.USER);
        testUser.setFirstName("Ivan");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("encryptedNumber");
        testCard.setOwner(testUser);
        testCard.setExpirationDate(LocalDate.now().plusYears(1));
        testCard.setStatus(CardStatus.ACTIVE);
        testCard.setBalance(0.0);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(testUser.getEmail());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createCard_shouldSaveEncryptedCardAndReturnResponse() throws Exception {
        CardRequest request = new CardRequest("1234567890123456", LocalDate.now().plusYears(2));

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(encryptionUtil.encrypt(anyString())).thenReturn("encryptedNumber");
        when(cardRepository.save(any())).thenReturn(testCard);
        when(encryptionUtil.decrypt(anyString())).thenReturn("1234567890123456");

        CardResponse response = cardService.createCard(request);

        assertNotNull(response);
        assertEquals(CardStatus.ACTIVE, response.getStatus());
        assertEquals("Ivan", response.getOwnerName());
        assertTrue(response.getMaskedCardNumber().startsWith("****"));

        verify(cardRepository).save(any());
    }

    @Test
    void getCardById_shouldReturnCardResponse_whenCardExistsAndOwnerMatches() throws Exception {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(encryptionUtil.decrypt(testCard.getCardNumber())).thenReturn("1234567890123456");

        CardResponse response = cardService.getCardById(1L);

        assertNotNull(response);
        assertEquals(CardStatus.ACTIVE, response.getStatus());
        verify(cardRepository).findById(1L);
    }

    @Test
    void blockCard_shouldChangeStatusToBlocked() throws Exception {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(encryptionUtil.decrypt(anyString())).thenReturn("1234567890123456");

        CardResponse response = cardService.blockCard(1L);

        assertEquals(CardStatus.BLOCKED, response.getStatus());
    }

    @Test
    void deleteCard_shouldCallRepositoryDelete() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        cardService.deleteCard(1L);

        verify(cardRepository).delete(testCard);
    }

    @Test
    void getMyCards_shouldReturnPagedCards() throws Exception {
        Card anotherCard = new Card();
        anotherCard.setCardNumber("encrypted2");
        anotherCard.setOwner(testUser);
        anotherCard.setStatus(CardStatus.ACTIVE);
        anotherCard.setExpirationDate(LocalDate.now().plusMonths(6));

        Page<Card> page = new PageImpl<>(List.of(testCard, anotherCard));
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cardRepository.findByOwnerId(eq(testUser.getId()), any())).thenReturn(page);
        when(encryptionUtil.decrypt(anyString())).thenReturn("1234567890123456");

        List<CardResponse> responses = cardService.getMyCards(null, 0, 10);

        assertEquals(2, responses.size());
    }

    @Test
    void getCardById_shouldThrowException_whenNotOwner() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        testCard.setOwner(anotherUser);

        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        assertThrows(AccessDeniedException.class, () -> cardService.getCardById(1L));
    }
}
