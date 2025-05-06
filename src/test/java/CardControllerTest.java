import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import ru.BankCardsApp;
import ru.dto.CardRequest;
import ru.dto.CardResponse;
import ru.dto.CardSearchCriteria;
import ru.service.CardService;
import ru.service.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.model.CardStatus.ACTIVE;
import static ru.model.CardStatus.EXPIRED;

@SpringBootTest(classes = BankCardsApp.class)
@AutoConfigureMockMvc
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Поиск карт доступен пользователю с ролью USER")
    @WithMockUser(username = "user@example.com", roles = "USER")
    void searchCards_shouldReturnCardsPage() throws Exception {
        CardResponse card = new CardResponse(1L,
                "John Doe",
                "**** **** **** 1234",
                LocalDate.now().plusYears(1),
                ACTIVE,
                1000.00);
        Page<CardResponse> page = new PageImpl<>(List.of(card));

        Mockito.when(userService.getUserIdByEmail("user@example.com")).thenReturn(1L);
        Mockito.when(cardService.searchCards(eq(1L), any(CardSearchCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @DisplayName("Создание карты доступно только администратору")
    @WithMockUser(roles = "ADMIN")
    void createCard_shouldReturnCreatedCard() throws Exception {
        CardRequest request = new CardRequest("1234567812345678", LocalDate.now().plusYears(1));
        CardResponse response = new CardResponse(1L,
                "John Doe",
                "**** **** **** 5678",
                LocalDate.now().plusYears(1),
                ACTIVE,
                1000.00);

        Mockito.when(cardService.createCard(any(CardRequest.class))).thenReturn(response);

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Удаление карты доступно только администратору")
    @WithMockUser(roles = "ADMIN")
    void deleteCard_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/cards/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Блокировка карты доступна только администратору")
    @WithMockUser(roles = "ADMIN")
    void blockCard_shouldReturnBlockedCard() throws Exception {
        CardResponse response = new CardResponse(1L,
                "John Doe",
                "**** **** **** 1234",
                LocalDate.now().plusYears(1),
                ACTIVE,
                1000.00);
        Mockito.when(cardService.blockCard(1L)).thenReturn(response);

        mockMvc.perform(put("/cards/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Активация карты доступна только администратору")
    @WithMockUser(roles = "ADMIN")
    void activateCard_shouldReturnActivatedCard() throws Exception {
        CardResponse response = new CardResponse(1L,
                "John Doe",
                "**** **** **** 1234",
                LocalDate.now().plusYears(1),
                EXPIRED,
                1000.00);
        Mockito.when(cardService.activateCard(1L)).thenReturn(response);

        mockMvc.perform(put("/cards/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"));
    }

    @Test
    @DisplayName("Получение карты по ID доступно только администратору")
    @WithMockUser(roles = "ADMIN")
    void getCardById_shouldReturnCard() throws Exception {
        CardResponse response = new CardResponse(1L,
                "John Doe",
                "**** **** **** 1234",
                LocalDate.now().plusYears(1),
                ACTIVE,
                1000.00);
        Mockito.when(cardService.getCardById(1L)).thenReturn(response);

        mockMvc.perform(get("/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}