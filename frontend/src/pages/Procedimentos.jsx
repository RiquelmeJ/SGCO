import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const schema = z.object({
  cpf: z.string().min(1, 'Selecione um paciente'),
  tipo: z.string().min(3, 'Tipo de procedimento obrigatório'),
  data: z.string().min(1, 'Data obrigatória'),
  valor: z.number({ coerce: true }).min(0, 'Valor não pode ser negativo'),
  formaPagamento: z.string(),
  status: z.string().min(1, 'Status obrigatório'),
  observacoes: z.string().optional()
});

const Procedimentos = () => {
  const [patients, setPatients] = useState([]);
  
  const { register, handleSubmit, reset, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      status: 'Agendado',
      formaPagamento: '1',
      data: new Date().toISOString().split('T')[0]
    }
  });

  useEffect(() => {
    axios.get('/api/pacientes').then(res => setPatients(res.data)).catch(console.error);
  }, []);

  const onSubmit = async (data) => {
    try {
      await axios.post('/api/procedimentos', {
        ...data,
        formaPagamento: parseInt(data.formaPagamento)
      });
      alert('Procedimento registrado com sucesso!');
      reset({ status: 'Agendado', formaPagamento: '1', data: new Date().toISOString().split('T')[0], tipo: '', valor: 0, observacoes: '', cpf: '' });
    } catch (e) {
      alert('Erro ao registrar procedimento.');
    }
  };

  return (
    <div className="section active">
      <div className="card">
        <div className="card-header">
          <h2 className="card-title">Registrar Novo Procedimento</h2>
        </div>
        <form onSubmit={handleSubmit(onSubmit)} className="form-grid">
          <div className="form-group full-width">
            <label>Paciente</label>
            <select {...register('cpf')}>
              <option value="">Selecione um paciente...</option>
              {patients.map(p => (
                <option key={p.cpf} value={p.cpf}>{p.nome} ({p.cpf})</option>
              ))}
            </select>
            {errors.cpf && <span className="error-message">{errors.cpf.message}</span>}
          </div>
          <div className="form-group">
            <label>Tipo de Procedimento</label>
            <input type="text" placeholder="Ex: Restauração" {...register('tipo')} />
            {errors.tipo && <span className="error-message">{errors.tipo.message}</span>}
          </div>
          <div className="form-group">
            <label>Data</label>
            <input type="date" {...register('data')} />
            {errors.data && <span className="error-message">{errors.data.message}</span>}
          </div>
          <div className="form-group">
            <label>Valor (R$)</label>
            <input type="number" step="0.01" {...register('valor')} />
            {errors.valor && <span className="error-message">{errors.valor.message}</span>}
          </div>
          <div className="form-group">
            <label>Forma de Pagamento</label>
            <select {...register('formaPagamento')}>
              <option value="1">Dinheiro</option>
              <option value="2">Cartão</option>
            </select>
          </div>
          <div className="form-group">
            <label>Status</label>
            <select {...register('status')}>
              <option value="Agendado">Agendado</option>
              <option value="Em Andamento">Em Andamento</option>
              <option value="Concluído">Concluído</option>
              <option value="Cancelado">Cancelado</option>
            </select>
            {errors.status && <span className="error-message">{errors.status.message}</span>}
          </div>
          <div className="form-group full-width">
            <label>Observações</label>
            <textarea rows="3" {...register('observacoes')}></textarea>
          </div>
          <div className="full-width" style={{ textAlign: 'right' }}>
            <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
              {isSubmitting ? 'Registrando...' : 'Registrar Procedimento'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Procedimentos;
