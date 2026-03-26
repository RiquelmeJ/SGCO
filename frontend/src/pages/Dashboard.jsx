import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { CalendarCheck, Users, Activity, AlertTriangle } from 'lucide-react';

const Dashboard = () => {
  const [stats, setStats] = useState({ consultasHoje: 0, totalPacientes: 0, totalProcedimentos: 0 });
  const [procedures, setProcedures] = useState([]);
  const [selectedProc, setSelectedProc] = useState(null);

  const loadData = async () => {
    try {
      const [statsRes, procRes] = await Promise.all([
        axios.get('/api/dashboard/stats'),
        axios.get('/api/procedimentos')
      ]);
      setStats(statsRes.data);
      setProcedures(procRes.data.slice(0, 8)); // latest 8
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleStatusChange = async (id, status) => {
    try {
      await axios.post('/api/procedimentos/update-status', { id, status });
      loadData();
    } catch (error) {
      console.error('Error updating status:', error);
    }
  };

  return (
    <div className="section active">
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon bg-blue"><CalendarCheck /></div>
          <div className="stat-details">
            <h3>Consultas Hoje</h3>
            <p>{stats.consultasHoje}</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon bg-green"><Users /></div>
          <div className="stat-details">
            <h3>Total Pacientes</h3>
            <p>{stats.totalPacientes}</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon bg-orange"><Activity /></div>
          <div className="stat-details">
            <h3>Total Procedimentos</h3>
            <p>{stats.totalProcedimentos}</p>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon bg-red"><AlertTriangle /></div>
          <div className="stat-details">
            <h3>Pendentes</h3>
            <p>0</p>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Próximos Atendimentos</h2>
          <button className="btn btn-outline">Ver Agenda Completa</button>
        </div>
        <table>
          <thead>
            <tr>
              <th>Paciente</th>
              <th>Horário</th>
              <th>Procedimento</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {procedures.map(p => (
              <tr key={p.id}>
                <td>{p.pacienteNome || 'N/A'} ({p.pacienteCpf || 'N/A'})</td>
                <td>{p.data ? new Date(p.data).toLocaleString('pt-BR') : 'N/A'}</td>
                <td>{p.tipo}</td>
                <td>
                  <select 
                    style={{ padding: '4px 8px', borderRadius: '4px', border: '1px solid var(--border)' }}
                    value={p.status}
                    onChange={(e) => handleStatusChange(p.id, e.target.value)}
                  >
                    <option value="Agendado">Agendado</option>
                    <option value="Em Andamento">Em Andamento</option>
                    <option value="Concluído">Concluído</option>
                    <option value="Cancelado">Cancelado</option>
                  </select>
                </td>
                <td>
                  <button className="btn btn-outline btn-sm" onClick={() => setSelectedProc(p)}>Detalhes</button>
                </td>
              </tr>
            ))}
            {procedures.length === 0 && (
              <tr><td colSpan="5" style={{ textAlign: 'center' }}>Nenhum procedimento recente.</td></tr>
            )}
          </tbody>
        </table>
      </div>

      {selectedProc && (
        <div className="modal-overlay" onClick={(e) => { if(e.target === e.currentTarget) setSelectedProc(null); }}>
          <div className="modal">
            <div className="modal-header">
              <h2 className="card-title">Detalhes do Atendimento</h2>
              <button className="btn btn-outline btn-sm" onClick={() => setSelectedProc(null)}>&times;</button>
            </div>
            <div>
              <p><strong>Paciente:</strong> {selectedProc.pacienteNome} ({selectedProc.pacienteCpf})</p>
              <p><strong>Data:</strong> {selectedProc.data ? new Date(selectedProc.data).toLocaleString('pt-BR') : 'N/A'}</p>
              <p><strong>Tipo:</strong> {selectedProc.tipo}</p>
              <p><strong>Valor:</strong> R$ {selectedProc.valor.toFixed(2)}</p>
              <p><strong>Forma Pagamento:</strong> {selectedProc.formaPagamento || 'N/A'}</p>
              <p><strong>Status:</strong> {selectedProc.status}</p>
              <hr style={{ margin: '15px 0', borderColor: 'var(--border)', borderStyle: 'solid', borderWidth: '1px 0 0 0' }} />
              <p><strong>Observações:</strong></p>
              <div style={{ background: '#f9f9f9', padding: '10px', borderRadius: '4px', borderLeft: '4px solid var(--primary)', marginTop: '5px' }}>
                {selectedProc.observacoes || 'Sem observações.'}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
