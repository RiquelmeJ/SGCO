document.addEventListener('DOMContentLoaded', () => {
    const API_BASE = '/api';
    let allPatients = []; // Cache for selection
    let dashboardProcedures = []; // Cache for detail views
    let isEditingPatient = false;
    let editingCpf = null;
    let patientToDeleteCpf = null;

    // Navigation Logic
    const navItems = document.querySelectorAll('.nav-item');
    const sections = document.querySelectorAll('.section');

    navItems.forEach(item => {
        item.addEventListener('click', (e) => {
            const sectionId = item.getAttribute('data-section');
            if (!sectionId) return;

            e.preventDefault();
            navItems.forEach(nav => nav.classList.remove('active'));
            item.classList.add('active');

            sections.forEach(section => {
                section.classList.remove('active');
                if (section.id === sectionId) {
                    section.classList.add('active');
                }
            });

            if (sectionId === 'patients') loadPatients();
            if (sectionId === 'dashboard') loadDashboard();
            if (sectionId === 'procedures') loadProceduresTab();
            if (sectionId === 'documents') loadDocuments();
        });
    });

    // Helper to format date to yyyy-mm-dd for input
    function formatDateForInput(dateInput) {
        if (!dateInput) return '';
        const d = new Date(dateInput);
        if (isNaN(d.getTime())) return '';
        return d.toISOString().split('T')[0];
    }

    // --- DASHBOARD LOADING ---

    async function loadDashboard() {
        try {
            const statsResp = await fetch(`${API_BASE}/dashboard/stats`);
            const stats = await statsResp.json();
            
            document.getElementById('stat-today').textContent = stats.consultasHoje || 0;
            document.getElementById('stat-patients').textContent = stats.totalPacientes || 0;
            document.getElementById('stat-total-proc').textContent = stats.totalProcedimentos || 0;

            const procResp = await fetch(`${API_BASE}/procedimentos`);
            dashboardProcedures = await procResp.json();
            const tableBody = document.getElementById('dashboard-proc-table');
            if (!tableBody) return;
            tableBody.innerHTML = '';

            if (dashboardProcedures.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center">Nenhum procedimento recente.</td></tr>';
            } else {
                dashboardProcedures.slice(0, 8).forEach(p => {
                    const row = document.createElement('tr');
                    const dateStr = p.data ? new Date(p.data).toLocaleString('pt-BR') : 'N/A';
                    
                    const statusOptions = ['Agendado', 'Em Andamento', 'Concluído', 'Cancelado'];
                    let optionsHtml = statusOptions.map(opt => `<option value="${opt}" ${p.status === opt ? 'selected' : ''}>${opt}</option>`).join('');

                    row.innerHTML = `
                        <td>${p.pacienteNome || 'N/A'} (${p.pacienteCpf || 'N/A'})</td>
                        <td>${dateStr}</td>
                        <td>${p.tipo}</td>
                        <td>
                            <select class="status-select" data-id="${p.id}" style="padding: 2px 5px; border-radius: 4px; border: 1px solid var(--border);">
                                ${optionsHtml}
                            </select>
                        </td>
                        <td><button class="btn btn-outline btn-sm dashboard-detail-btn" data-id="${p.id}">Detalhes</button></td>
                    `;
                    tableBody.appendChild(row);
                });

                document.querySelectorAll('.status-select').forEach(select => {
                    select.addEventListener('change', (e) => updateProcedureStatus(e.target.getAttribute('data-id'), e.target.value));
                });

                document.querySelectorAll('.dashboard-detail-btn').forEach(btn => {
                    btn.addEventListener('click', () => viewProcedureDetails(btn.getAttribute('data-id')));
                });
            }
        } catch (error) {
            console.error('Erro ao carregar dashboard:', error);
        }
    }

    async function updateProcedureStatus(id, newStatus) {
        if (!id) return;
        try {
            const response = await fetch(`${API_BASE}/procedimentos/update-status`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: parseInt(id), status: newStatus })
            });
            const result = await response.json();
            if (result.status === 'success') {
                console.log('Status atualizado');
            }
        } catch (error) {
            console.error('Erro ao atualizar status:', error);
        }
    }

    function viewProcedureDetails(id) {
        const proc = dashboardProcedures.find(p => p.id == id);
        if (!proc) return;

        const content = document.getElementById('view-procedure-content');
        content.innerHTML = `
            <div class="proc-details-view">
                <p><strong>Paciente:</strong> ${proc.pacienteNome} (${proc.pacienteCpf})</p>
                <p><strong>Data:</strong> ${proc.data ? new Date(proc.data).toLocaleString('pt-BR') : 'N/A'}</p>
                <p><strong>Tipo:</strong> ${proc.tipo}</p>
                <p><strong>Valor:</strong> R$ ${proc.valor.toFixed(2)}</p>
                <p><strong>Forma de Pagamento:</strong> ${proc.formaPagamento || 'N/A'}</p>
                <p><strong>Status Atual:</strong> ${proc.status}</p>
                <hr style="margin: 10px 0; border: 0; border-top: 1px solid var(--border);">
                <p><strong>Observações:</strong></p>
                <div style="background: #f9f9f9; padding: 10px; border-radius: 4px; border-left: 4px solid var(--primary); margin-top: 5px;">
                    ${proc.observacoes || 'Sem observações.'}
                </div>
            </div>
        `;
        document.getElementById('modal-view-procedure').style.display = 'flex';
    }

    // --- PATIENTS LOADING & ACTIONS ---

    async function loadPatients() {
        const tableBody = document.getElementById('patient-table-body');
        if (!tableBody) return;
        tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center">Carregando...</td></tr>';

        try {
            const response = await fetch(`${API_BASE}/pacientes`);
            allPatients = await response.json();

            tableBody.innerHTML = '';
            if (allPatients.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center">Nenhum paciente cadastrado.</td></tr>';
                return;
            }

            allPatients.forEach(p => {
                const row = document.createElement('tr');
                const birth = p.dataNascimento ? new Date(p.dataNascimento).toLocaleDateString('pt-BR') : 'N/A';
                row.innerHTML = `
                    <td>${p.nome}</td>
                    <td>${p.cpf}</td>
                    <td>${birth}</td>
                    <td>${p.contato || 'N/A'}</td>
                    <td>
                        <button class="btn btn-outline btn-sm view-btn" data-cpf="${p.cpf}" title="Visualizar"><i class="fas fa-eye"></i></button>
                        <button class="btn btn-outline btn-sm edit-btn" data-cpf="${p.cpf}" title="Editar"><i class="fas fa-edit"></i></button>
                        <button class="btn btn-outline btn-sm delete-btn" data-cpf="${p.cpf}" title="Excluir" style="color: var(--danger); border-color: var(--danger)"><i class="fas fa-trash"></i></button>
                    </td>
                `;
                tableBody.appendChild(row);
            });

            document.querySelectorAll('.view-btn').forEach(btn => {
                btn.addEventListener('click', () => viewPatient(btn.getAttribute('data-cpf')));
            });

            document.querySelectorAll('.edit-btn').forEach(btn => {
                btn.addEventListener('click', () => openEditPatientModal(btn.getAttribute('data-cpf')));
            });

            document.querySelectorAll('.delete-btn').forEach(btn => {
                btn.addEventListener('click', () => {
                    patientToDeleteCpf = btn.getAttribute('data-cpf');
                    document.getElementById('modal-confirm-delete').style.display = 'flex';
                });
            });

        } catch (error) {
            console.error('Erro ao carregar pacientes:', error);
            tableBody.innerHTML = '<tr><td colspan="5" style="text-align:center; color: var(--danger)">Erro de conexão.</td></tr>';
        }
    }

    document.getElementById('btn-confirm-delete')?.addEventListener('click', async () => {
        if (!patientToDeleteCpf) return;
        try {
            const response = await fetch(`${API_BASE}/pacientes/${patientToDeleteCpf}`, {
                method: 'DELETE'
            });
            const result = await response.json();
            if (result.status === 'success') {
                alert('Paciente excluído!');
                document.getElementById('modal-confirm-delete').style.display = 'none';
                loadPatients();
                loadDashboard();
            } else {
                alert(`Erro ao excluir: ${result.message}`);
                document.getElementById('modal-confirm-delete').style.display = 'none';
            }
        } catch (error) {
            console.error('Erro ao excluir:', error);
            alert('Falha na comunicação com o servidor.');
            document.getElementById('modal-confirm-delete').style.display = 'none';
        }
    });

    function openEditPatientModal(cpf) {
        const patient = allPatients.find(p => p.cpf === cpf);
        if (!patient) return;

        isEditingPatient = true;
        editingCpf = cpf;

        document.getElementById('p-name').value = patient.nome || '';
        document.getElementById('p-cpf').value = patient.cpf || '';
        document.getElementById('p-cpf').disabled = true; 
        document.getElementById('p-birth').value = formatDateForInput(patient.dataNascimento);
        document.getElementById('p-contact').value = patient.contato || '';
        document.getElementById('p-history').value = patient.anamnese?.historicoClinico || '';
        
        const allergiesField = document.getElementById('p-allergies');
        if (allergiesField) allergiesField.value = patient.anamnese?.alergias || '';
        const obsField = document.getElementById('p-obs');
        if (obsField) obsField.value = patient.anamnese?.observacoesGerais || '';

        const title = document.querySelector('#modal-patient .card-title');
        if (title) title.textContent = 'Editar Paciente';
        document.getElementById('modal-patient').style.display = 'flex';
    }

    function viewPatient(cpf) {
        const patient = allPatients.find(p => p.cpf === cpf);
        if (!patient) return;

        const content = document.getElementById('view-patient-content');
        content.innerHTML = `
            <div class="patient-details-view">
                <p><strong>Nome:</strong> ${patient.nome}</p>
                <p><strong>CPF:</strong> ${patient.cpf}</p>
                <p><strong>Nascimento:</strong> ${patient.dataNascimento ? new Date(patient.dataNascimento).toLocaleDateString('pt-BR') : 'N/A'}</p>
                <p><strong>Contato:</strong> ${patient.contato || 'N/A'}</p>
                <hr style="margin: 10px 0; border: 0; border-top: 1px solid var(--border);">
                <p><strong>Anamnese:</strong> ${patient.anamnese?.historicoClinico || 'N/A'}</p>
                <p><strong>Alergias:</strong> ${patient.anamnese?.alergias || 'Nenhuma'}</p>
                <p><strong>Observações:</strong> ${patient.anamnese?.observacoesGerais || 'Nenhuma'}</p>
            </div>
        `;
        document.getElementById('modal-view-patient').style.display = 'flex';
    }

    async function loadProceduresTab() {
        const select = document.getElementById('proc-patient');
        if (!select) return;
        if (allPatients.length === 0) {
            const resp = await fetch(`${API_BASE}/pacientes`);
            allPatients = await resp.json();
        }

        select.innerHTML = '<option value="">Selecione um paciente...</option>';
        allPatients.forEach(p => {
            const opt = document.createElement('option');
            opt.value = p.cpf;
            opt.textContent = `${p.nome} (${p.cpf})`;
            select.appendChild(opt);
        });
        
        document.getElementById('proc-date').valueAsDate = new Date();
    }

    async function loadDocuments() {
        const tableBody = document.getElementById('documents-table-body');
        if (!tableBody) return;
        tableBody.innerHTML = '<tr><td colspan="4" style="text-align:center">Carregando...</td></tr>';

        try {
            const response = await fetch(`${API_BASE}/documentos`);
            const docs = await response.json();
            tableBody.innerHTML = '';

            if (docs.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="4" style="text-align:center">Nenhum documento gerado recentemente.</td></tr>';
            } else {
                docs.forEach(d => {
                    const row = document.createElement('tr');
                    const dateStr = d.data ? new Date(d.data).toLocaleString('pt-BR') : 'N/A';
                    row.innerHTML = `
                        <td>${d.pacienteNome}</td>
                        <td>${d.tipo}</td>
                        <td>${dateStr}</td>
                        <td><button class="btn btn-outline btn-sm"><i class="fas fa-download"></i> PDF</button></td>
                    `;
                    tableBody.appendChild(row);
                });
            }
        } catch (error) {
            console.error('Erro ao carregar documentos:', error);
        }
    }

    // --- MODAL MANAGEMENT ---

    function setupModal(modalId, btnId, closeClass = 'close-modal-btn') {
        const modal = document.getElementById(modalId);
        if (!modal) return;
        const btn = btnId ? document.getElementById(btnId) : null;
        const closeBtns = modal.querySelectorAll(`.${closeClass}`);

        if (btn) btn.addEventListener('click', () => {
            isEditingPatient = false;
            editingCpf = null;
            const form = modal.querySelector('form');
            if (form) {
                form.reset();
                const cpfInput = form.querySelector('#p-cpf');
                if (cpfInput) cpfInput.disabled = false;
                const title = modal.querySelector('.card-title');
                if (title && modalId === 'modal-patient') title.textContent = 'Novo Paciente';
            }
            modal.style.display = 'flex';
        });

        closeBtns.forEach(cb => cb.addEventListener('click', () => modal.style.display = 'none'));
        
        window.addEventListener('click', (e) => {
            if (e.target === modal) modal.style.display = 'none';
        });
    }

    setupModal('modal-patient', 'btn-new-patient', 'btn-outline');
    setupModal('modal-atestado', 'card-atestado');
    setupModal('modal-encaminhamento', 'card-encaminhamento');
    setupModal('modal-receituario', 'card-receituario');
    setupModal('modal-view-patient', null);
    setupModal('modal-view-procedure', null);
    setupModal('modal-confirm-delete', null);
    
    document.querySelectorAll('.close-modal-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const overlay = btn.closest('.modal-overlay');
            if (overlay) overlay.style.display = 'none';
        });
    });

    const closePat = document.getElementById('close-modal');
    if (closePat) closePat.addEventListener('click', () => document.getElementById('modal-patient').style.display = 'none');

    // --- FORM SUBMISSIONS ---

    const postData = async (url, data, successMsg, method = 'POST') => {
        try {
            const response = await fetch(`${API_BASE}${url}`, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });
            const result = await response.json();
            if (result.status === 'success') {
                if (successMsg) alert(successMsg);
                return true;
            } else {
                alert('Erro: ' + (result.message || 'Desconhecido'));
                return false;
            }
        } catch (error) {
            console.error('Erro:', error);
            alert('Falha na comunicação com o servidor.');
            return false;
        }
    };

    document.getElementById('form-patient')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            nome: document.getElementById('p-name').value,
            cpf: document.getElementById('p-cpf').value,
            dataNascimento: document.getElementById('p-birth').value,
            contato: document.getElementById('p-contact').value,
            historico: document.getElementById('p-history').value,
            alergias: document.getElementById('p-allergies')?.value || '',
            observacoes: document.getElementById('p-obs')?.value || ''
        };

        const url = '/pacientes';
        const method = isEditingPatient ? 'PUT' : 'POST';
        const msg = isEditingPatient ? 'Paciente atualizado!' : 'Paciente cadastrado!';

        if (await postData(url, data, msg, method)) {
            document.getElementById('modal-patient').style.display = 'none';
            e.target.reset();
            loadPatients();
            loadDashboard();
        }
    });

    document.getElementById('form-procedure')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            cpf: document.getElementById('proc-patient').value,
            tipo: document.getElementById('proc-type').value,
            data: document.getElementById('proc-date').value,
            valor: parseFloat(document.getElementById('proc-value').value),
            formaPagamento: parseInt(document.getElementById('proc-payment').value),
            status: document.getElementById('proc-status').value,
            observacoes: document.getElementById('proc-obs').value
        };
        if (await postData('/procedimentos', data, 'Procedimento registrado!')) {
            e.target.reset();
            loadDashboard();
        }
    });

    document.getElementById('form-atestado')?.addEventListener('submit', async (e) => {
        e.preventDefault();
        const data = {
            cpf: document.getElementById('atest-cpf').value,
            dias: parseInt(document.getElementById('atest-dias').value),
            motivo: document.getElementById('atest-motivo').value
        };
        if (await postData('/documentos/atestado', data, 'Atestado gerado com sucesso!')) {
            document.getElementById('modal-atestado').style.display = 'none';
            e.target.reset();
            loadDocuments();
        }
    });

    // Initial Load
    loadDashboard();
});
