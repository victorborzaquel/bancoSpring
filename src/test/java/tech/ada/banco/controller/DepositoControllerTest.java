package tech.ada.banco.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DepositoControllerTest {
    @Autowired
    private MockMvc mvc;
    private final String baseUri = "/deposito";
//
//    @Test
//    void testDepositoPositivo() throws Exception {
//        Conta contaBase = new Conta(ModalidadeConta.CC, null);
//        String response =
//                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
//                                .param("valor", "10")
//                                .contentType(MediaType.APPLICATION_JSON))
//                        .andDo(print())
//                        .andExpect(status().isOk())
//                        .andReturn().getResponse().getContentAsString();
//
//        assertEquals("10", response);
//        assertEquals(BigDecimal.TEN, contaBase.getSaldo());
//    }
//
//    @Test
//    void testDepositoNegativo() throws Exception {
//        Conta contaBase = new Conta(ModalidadeConta.CC, null);
//        String response =
//                mvc.perform(post(baseUri + "/" + contaBase.getNumeroConta())
//                                .param("valor", "-10")
//                                .contentType(MediaType.APPLICATION_JSON))
//                        .andDo(print())
//                        .andExpect(status().isBadRequest())
//                        .andReturn().getResponse().getErrorMessage();
//
//        assertEquals("Valor informado está inválido.", response);
//        assertEquals(BigDecimal.ZERO, contaBase.getSaldo());
//    }
}