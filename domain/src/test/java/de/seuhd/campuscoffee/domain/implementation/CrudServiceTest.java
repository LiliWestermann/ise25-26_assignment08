package de.seuhd.campuscoffee.domain.implementation;

import de.seuhd.campuscoffee.domain.model.objects.DomainModel;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrudServiceImplTest {

    static class TestDomain implements DomainModel<UUID> {
        private UUID id;

        @Override
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }

    static class TestCrudService extends CrudServiceImpl<TestDomain, UUID> {

        private final CrudDataService<TestDomain, UUID> dataService;

        protected TestCrudService(CrudDataService<TestDomain, UUID> dataService) {
            super(TestDomain.class);
            this.dataService = dataService;
        }

        @Override
        protected CrudDataService<TestDomain, UUID> dataService() {
            return dataService;
        }
    }

    private CrudDataService<TestDomain, UUID> mockDataService;
    private TestCrudService service;

    private TestDomain domain;
    private UUID id;

    @BeforeEach
    void setUp() {
        mockDataService = mock(CrudDataService.class);
        service = new TestCrudService(mockDataService);

        domain = new TestDomain();
        id = UUID.randomUUID();
        domain.setId(id);
    }

    @Test
    void testGetAll() {
        //given
        when(mockDataService.getAll()).thenReturn(List.of(domain));

        //when
        List<TestDomain> all = service.getAll();

        //then
        assertThat(all).containsExactly(domain);
        verify(mockDataService).getAll();
    }

    @Test
    void testGetById() {
        //given
        when(mockDataService.getById(id)).thenReturn(domain);

        //when
        TestDomain result = service.getById(id);

        //then
        assertThat(result).isEqualTo(domain);
        verify(mockDataService).getById(id);
    }

    @Test
    void testUpsertCreateNew() {
        //given
        domain.setId(null);
        when(mockDataService.upsert(domain)).thenReturn(domain);

        //when
        TestDomain result = service.upsert(domain);

        //then
        assertThat(result).isEqualTo(domain);
        verify(mockDataService).upsert(domain);
    }

    @Test
    void testUpsertUpdateExisting() {
        //given
        when(mockDataService.getById(id)).thenReturn(domain);
        when(mockDataService.upsert(domain)).thenReturn(domain);

        //when
        TestDomain result = service.upsert(domain);

        //then
        assertThat(result).isEqualTo(domain);
        verify(mockDataService).getById(id);
        verify(mockDataService).upsert(domain);
    }

    @Test
    void testDelete() {
        //when
        service.delete(id);

        //then
        verify(mockDataService).delete(id);
    }

    @Test
    void testClear() {
        //when
        service.clear();

        //then
        verify(mockDataService).clear();
    }
}

