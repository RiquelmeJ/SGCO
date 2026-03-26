import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useForm, useFieldArray } from 'react-hook-form';
import { FileText, Share2, Pill, Download } from 'lucide-react';

const Documentos = () => {
  const [documents, setDocuments] = useState([]);
  const [modalType, setModalType] = useState(null);

  const { register: regAtestado, handleSubmit: handleAtestado, reset: resetAtestado } = useForm();
  const { register: regEncaminhamento, handleSubmit: handleEncaminhamento, reset: resetEncaminhamento } = useForm();
  const { register: regReceituario, control, handleSubmit: handleReceituario, reset: resetReceituario } = useForm({
    defaultValues: { itens: [{ medicamento: '', posologia: '', duracao: '' }] }
  });
  
  const { fields, append, remove } = useFieldArray({ control, name: "itens" });

  const loadDocs = async () => {
    try {
      const res = await axios.get('/api/documentos');
      setDocuments(res.data);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => { loadDocs(); }, []);

  const onAtestado = async (data) => {
    try {
      await axios.post('/api/documentos/atestado', {
        ...data,
        dias: parseInt(data.dias)
      });
      alert('Atestado gerado!');
      setModalType(null);
      resetAtestado();
      loadDocs();
    } catch (e) { alert('Erro ao gerar atestado'); }
  };

  const onEncaminhamento = async (data) => {
    try {
      await axios.post('/api/documentos/encaminhamento', data);
      alert('Encaminhamento gerado!');
      setModalType(null);
      resetEncaminhamento();
      loadDocs();
    } catch (e) { alert('Erro ao gerar encaminhamento'); }
  };

  const onReceituario = async (data) => {
    try {
      await axios.post('/api/documentos/receituario', data);
      alert('Receituário gerado!');
      setModalType(null);
      resetReceituario();
      loadDocs();
    } catch (e) { alert('Erro ao gerar receituário'); }
  };

  return (
    <div className="section active">
      <div className="stats-grid">
        <div className="stat-card" style={{ cursor: 'pointer' }} onClick={() => setModalType('atestado')}>
          <div className="stat-icon bg-blue"><FileText /></div>
          <div className="stat-details">
            <h3>Atestado</h3>
            <p style={{ fontSize: '0.9rem' }}>Gerar atestado médico</p>
          </div>
        </div>
        <div className="stat-card" style={{ cursor: 'pointer' }} onClick={() => setModalType('encaminhamento')}>
          <div className="stat-icon bg-green"><Share2 /></div>
          <div className="stat-details">
            <h3>Encaminhamento</h3>
            <p style={{ fontSize: '0.9rem' }}>Para outros especialistas</p>
          </div>
        </div>
        <div className="stat-card" style={{ cursor: 'pointer' }} onClick={() => setModalType('receituario')}>
          <div className="stat-icon bg-orange"><Pill /></div>
          <div className="stat-details">
            <h3>Receituário</h3>
            <p style={{ fontSize: '0.9rem' }}>Prescrição inteligente</p>
          </div>
        </div>
      </div>
      
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Documentos Recentes</h2>
        </div>
        <table>
          <thead>
            <tr>
              <th>Paciente</th>
              <th>Tipo</th>
              <th>Data</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {documents.length === 0 ? (
              <tr><td colSpan="4" style={{ textAlign: 'center' }}>Nenhum documento gerado recentemente.</td></tr>
            ) : documents.map((d, i) => (
              <tr key={i}>
                <td>{d.pacienteNome}</td>
                <td>{d.tipo}</td>
                <td>{d.data ? new Date(d.data).toLocaleString('pt-BR') : 'N/A'}</td>
                <td><button className="btn btn-outline btn-sm"><Download size={14} style={{ marginRight: 4 }}/> PDF</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {modalType === 'atestado' && (
        <div className="modal-overlay" onClick={(e) => { if(e.target === e.currentTarget) setModalType(null); }}>
          <div className="modal">
            <div className="modal-header">
              <h2 className="card-title">Gerar Atestado Rápido</h2>
              <button className="btn btn-outline btn-sm" onClick={() => setModalType(null)}>&times;</button>
            </div>
            <form onSubmit={handleAtestado(onAtestado)} className="form-grid">
              <div className="form-group full-width">
                <label>CPF do Paciente *</label>
                <input type="text" required {...regAtestado('cpf')} />
              </div>
              <div className="form-group">
                <label>Dias de Repouso *</label>
                <input type="number" required defaultValue="1" {...regAtestado('dias')} />
              </div>
              <div className="form-group">
                <label>Motivo</label>
                <input type="text" placeholder="Ex: Necessidade de repouso" {...regAtestado('motivo')} />
              </div>
              <div className="full-width" style={{ textAlign: 'right' }}>
                <button type="submit" className="btn btn-primary">Gerar PDF</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {modalType === 'encaminhamento' && (
        <div className="modal-overlay" onClick={(e) => { if(e.target === e.currentTarget) setModalType(null); }}>
          <div className="modal">
            <div className="modal-header">
              <h2 className="card-title">Emitir Encaminhamento</h2>
              <button className="btn btn-outline btn-sm" onClick={() => setModalType(null)}>&times;</button>
            </div>
            <form onSubmit={handleEncaminhamento(onEncaminhamento)} className="form-grid">
              <div className="form-group full-width">
                <label>CPF do Paciente *</label>
                <input type="text" required {...regEncaminhamento('cpf')} />
              </div>
              <div className="form-group">
                <label>Especialidade Destino</label>
                <input type="text" placeholder="Ex: Ortodontista" {...regEncaminhamento('especialidade')} />
              </div>
              <div className="form-group">
                <label>Motivo / Observações</label>
                <input type="text" {...regEncaminhamento('motivo')} />
              </div>
              <div className="full-width" style={{ textAlign: 'right' }}>
                <button type="submit" className="btn btn-primary">Gerar PDF</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {modalType === 'receituario' && (
        <div className="modal-overlay" onClick={(e) => { if(e.target === e.currentTarget) setModalType(null); }}>
          <div className="modal" style={{ maxHeight: '90vh', overflowY: 'auto' }}>
            <div className="modal-header">
              <h2 className="card-title">Prescrição Inteligente</h2>
              <button className="btn btn-outline btn-sm" onClick={() => setModalType(null)}>&times;</button>
            </div>
            <form onSubmit={handleReceituario(onReceituario)} className="form-grid">
              <div className="form-group full-width">
                <label>CPF do Paciente *</label>
                <input type="text" required {...regReceituario('cpf')} />
              </div>
              
              <div className="full-width">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }}>
                  <label>Medicamentos</label>
                  <button type="button" className="btn btn-outline btn-sm" onClick={() => append({ medicamento: '', posologia: '', duracao: '' })}>+ Adicionar</button>
                </div>
                
                {fields.map((field, index) => (
                  <div key={field.id} style={{ border: '1px solid var(--border)', padding: '15px', borderRadius: '8px', marginBottom: '10px', position: 'relative' }}>
                    {index > 0 && <button type="button" onClick={() => remove(index)} style={{ position: 'absolute', top: 5, right: 5, background: 'none', border: 'none', color: 'var(--danger)', cursor: 'pointer' }}><Trash2 size={16} /></button>}
                    
                    <div style={{ marginBottom: '10px' }}>
                      <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Nome do Remédio</label>
                      <input type="text" style={{ width: '100%', padding: '8px', border: '1px solid var(--border)', borderRadius: '4px' }} required {...regReceituario(`itens.${index}.medicamento`)} />
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                      <div>
                        <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Posologia</label>
                        <input type="text" placeholder="Ex: 1 comp 8/8h" style={{ width: '100%', padding: '8px', border: '1px solid var(--border)', borderRadius: '4px' }} required {...regReceituario(`itens.${index}.posologia`)} />
                      </div>
                      <div>
                        <label style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Duração</label>
                        <input type="text" placeholder="Ex: 5 dias" style={{ width: '100%', padding: '8px', border: '1px solid var(--border)', borderRadius: '4px' }} required {...regReceituario(`itens.${index}.duracao`)} />
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              <div className="full-width" style={{ textAlign: 'right', marginTop: '10px' }}>
                <button type="submit" className="btn btn-primary">Finalizar e Gerar PDF</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Documentos;
