import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Filter, LogOut, Plus, Edit, Eye, Trash2 } from 'lucide-react';

const validateCPF = (cpf) => {
  cpf = cpf.replace(/[^\d]+/g, '');
  if (cpf.length !== 11 || !!cpf.match(/(\d)\1{10}/)) return false;
  let t = 0; let d = 0;
  for (let c = 0; c < 9; t += parseInt(cpf.charAt(c)) * (10 - c), c++);
  d = 11 - (t % 11);
  if (d === 10 || d === 11) d = 0;
  if (d !== parseInt(cpf.charAt(9))) return false;
  t = 0; d = 0;
  for (let c = 0; c < 10; t += parseInt(cpf.charAt(c)) * (11 - c), c++);
  d = 11 - (t % 11);
  if (d === 10 || d === 11) d = 0;
  if (d !== parseInt(cpf.charAt(10))) return false;
  return true;
};

const maskCPF = (value) => {
  return value
    .replace(/\D/g, '')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})/, '$1-$2')
    .replace(/(-\d{2})\d+?$/, '$1');
};

const makeSchema = (isEdit) => z.object({
  nome: z.string().min(3, 'Nome deve ter no mínimo 3 caracteres'),
  cpf: z.string().min(11, 'CPF obrigatório').refine(validateCPF, 'CPF inválido'),
  dataNascimento: z.string().optional(),
  contato: z.string().optional(),
  historico: z.string().optional(),
  alergias: z.string().optional(),
  observacoes: z.string().optional(),
});

const Pacientes = () => {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalType, setModalType] = useState(null); // 'create', 'edit', 'view', 'delete'
  const [selectedPatient, setSelectedPatient] = useState(null);
  const [serverError, setServerError] = useState(null);

  const { register, handleSubmit, reset, setValue, watch, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(makeSchema(modalType === 'edit'))
  });

  const cpfValue = watch('cpf');

  useEffect(() => {
    if (cpfValue && modalType === 'create') {
      const masked = maskCPF(cpfValue);
      if (cpfValue !== masked) {
        setValue('cpf', masked);
      }
    }
  }, [cpfValue, setValue, modalType]);

  const loadPatients = async () => {
    setLoading(true);
    try {
      const res = await axios.get('/api/pacientes');
      setPatients(res.data);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadPatients(); }, []);

  const openCreate = () => {
    setServerError(null);
    setModalType('create');
    reset({ nome:'', cpf:'', dataNascimento:'', contato:'', historico:'', alergias:'', observacoes:'' });
  };

  const openEdit = (p) => {
    setServerError(null);
    setSelectedPatient(p);
    setModalType('edit');
    const bd = p.dataNascimento ? new Date(p.dataNascimento).toISOString().split('T')[0] : '';
    reset({
      nome: p.nome,
      cpf: p.cpf,
      dataNascimento: bd,
      contato: p.contato || '',
      historico: p.anamnese?.historicoClinico || '',
      alergias: p.anamnese?.alergias || '',
      observacoes: p.anamnese?.observacoesGerais || ''
    });
  };

  const onSubmit = async (data) => {
    setServerError(null);
    try {
      if (modalType === 'create') {
        data.cpf = data.cpf.replace(/\D/g, ''); // send numbers only or keep formatting depending on back. keeping formatting is fine if backend handles it, but let's strip to be safe, or just keep it since DAO expects string. Let's send raw to keep consistency with mask.
        const res = await axios.post('/api/pacientes', data);
        if (res.data.status === 'success') {
          setModalType(null);
          loadPatients();
        } else {
          setServerError(res.data.message);
        }
      } else {
        await axios.put('/api/pacientes', { ...data, cpf: selectedPatient.cpf });
        setModalType(null);
        loadPatients();
      }
    } catch (err) {
      if (err.response?.status === 400 || err.response?.status === 409) {
        setServerError(err.response.data.message || 'Erro ao salvar. Verifique se o CPF já está cadastrado.');
      } else {
        setServerError('Erro interno no servidor.');
      }
    }
  };

  const handleDelete = async () => {
    try {
      await axios.delete(`/api/pacientes/${selectedPatient.cpf}`);
      setModalType(null);
      loadPatients();
    } catch (err) {
      alert('Não foi possível excluir. Pode haver procedimentos atrelados.');
      setModalType(null);
    }
  };

  return (
    <div className="section active">
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Base de Pacientes</h2>
          <button className="btn btn-primary" onClick={openCreate}>
            <Plus size={16} /> Novo Paciente
          </button>
        </div>
        <div className="search-bar" style={{ marginBottom: '1.5rem', width: '100%' }}>
          <Filter size={18} />
          <input type="text" placeholder="Filtrar..." />
        </div>
        <table>
          <thead>
            <tr>
              <th>Nome</th>
              <th>CPF</th>
              <th>Nascimento</th>
              <th>Contato</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {loading ? <tr><td colSpan="5" style={{textAlign:'center'}}>Carregando...</td></tr> : 
              patients.length === 0 ? <tr><td colSpan="5" style={{textAlign:'center'}}>Nenhum paciente.</td></tr> :
              patients.map(p => (
                <tr key={p.cpf}>
                  <td>{p.nome}</td>
                  <td>{p.cpf}</td>
                  <td>{p.dataNascimento ? new Date(p.dataNascimento).toLocaleDateString('pt-BR') : 'N/A'}</td>
                  <td>{p.contato || 'N/A'}</td>
                  <td style={{ display: 'flex', gap: '5px' }}>
                    <button className="btn btn-outline btn-sm" onClick={() => { setSelectedPatient(p); setModalType('view'); }}><Eye size={14}/></button>
                    <button className="btn btn-outline btn-sm" onClick={() => openEdit(p)}><Edit size={14}/></button>
                    <button className="btn btn-outline btn-sm" style={{ color: 'var(--danger)', borderColor: 'var(--danger)' }} onClick={() => { setSelectedPatient(p); setModalType('delete'); }}><Trash2 size={14}/></button>
                  </td>
                </tr>
              ))
            }
          </tbody>
        </table>
      </div>

      {(modalType === 'create' || modalType === 'edit') && (
        <div className="modal-overlay" onClick={(e) => { if(e.target === e.currentTarget) setModalType(null); }}>
          <div className="modal">
            <div className="modal-header">
              <h2 className="card-title">{modalType === 'create' ? 'Cadastrar Novo Paciente' : 'Editar Paciente'}</h2>
              <button className="btn btn-outline btn-sm" onClick={() => setModalType(null)}>&times;</button>
            </div>
            
            {serverError && <div style={{ color: '#fff', marginBottom: '1rem', padding: '0.5rem', background: 'var(--danger)', borderRadius: '8px' }}>{serverError}</div>}
            
            <form onSubmit={handleSubmit(onSubmit)} className="form-grid">
              <div className="form-group">
                <label>Nome Completo *</label>
                <input type="text" {...register('nome')} />
                {errors.nome && <span className="error-message">{errors.nome.message}</span>}
              </div>
              <div className="form-group">
                <label>CPF *</label>
                <input type="text" placeholder="000.000.000-00" {...register('cpf')} disabled={modalType === 'edit'} />
                {errors.cpf && <span className="error-message">{errors.cpf.message}</span>}
              </div>
              <div className="form-group">
                <label>Data de Nascimento</label>
                <input type="date" {...register('dataNascimento')} />
              </div>
              <div className="form-group">
                <label>Contato</label>
                <input type="text" {...register('contato')} />
              </div>
              <div className="form-group full-width">
                <label>Histórico Clínico</label>
                <textarea rows="2" {...register('historico')}></textarea>
              </div>
              <div className="form-group">
                <label>Alergias</label>
                <input type="text" {...register('alergias')} />
              </div>
              <div className="form-group">
                <label>Observações</label>
                <input type="text" {...register('observacoes')} />
              </div>
              <div className="full-width" style={{ textAlign: 'right', marginTop: '1rem' }}>
                <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                  {isSubmitting ? 'Salvando...' : 'Salvar Paciente'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {modalType === 'view' && selectedPatient && (
        <div className="modal-overlay" onClick={(e) => { if(e.target === e.currentTarget) setModalType(null); }}>
          <div className="modal">
            <div className="modal-header">
              <h2 className="card-title">Detalhes do Paciente</h2>
              <button className="btn btn-outline btn-sm" onClick={() => setModalType(null)}>&times;</button>
            </div>
            <div>
              <p><strong>Nome:</strong> {selectedPatient.nome}</p>
              <p><strong>CPF:</strong> {selectedPatient.cpf}</p>
              <p><strong>Nascimento:</strong> {selectedPatient.dataNascimento ? new Date(selectedPatient.dataNascimento).toLocaleDateString('pt-BR') : 'N/A'}</p>
              <p><strong>Contato:</strong> {selectedPatient.contato || 'N/A'}</p>
              <hr style={{ margin: '15px 0' }} />
              <p><strong>Anamnese:</strong> {selectedPatient.anamnese?.historicoClinico || 'N/A'}</p>
              <p><strong>Alergias:</strong> {selectedPatient.anamnese?.alergias || 'Nenhuma'}</p>
              <p><strong>Observações:</strong> {selectedPatient.anamnese?.observacoesGerais || 'Nenhuma'}</p>
            </div>
          </div>
        </div>
      )}

      {modalType === 'delete' && selectedPatient && (
        <div className="modal-overlay" onClick={(e) => { if(e.target === e.currentTarget) setModalType(null); }}>
          <div className="modal" style={{ maxWidth: '400px', textAlign: 'center' }}>
            <h2 className="card-title" style={{ marginBottom: '1rem' }}>Confirmar Exclusão</h2>
            <p>Tem certeza que deseja excluir o paciente {selectedPatient.nome}? Esta ação não pode ser desfeita.</p>
            <div style={{ marginTop: '1.5rem', display: 'flex', gap: '10px', justifyContent: 'center' }}>
              <button className="btn btn-outline" onClick={() => setModalType(null)}>Cancelar</button>
              <button className="btn btn-primary" style={{ background: 'var(--danger)' }} onClick={handleDelete}>Excluir</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Pacientes;
