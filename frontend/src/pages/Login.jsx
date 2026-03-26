import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Shield } from 'lucide-react';
import axios from 'axios';

const schema = z.object({
  username: z.string().min(1, 'Usuário é obrigatório'),
  password: z.string().min(1, 'Senha é obrigatória'),
});

const Login = ({ onLogin }) => {
  const [error, setError] = useState(null);
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm({
    resolver: zodResolver(schema)
  });

  const onSubmit = async (data) => {
    setError(null);
    try {
      const response = await axios.post('/api/login', data);
      if (response.data.status === 'success') {
        onLogin();
      } else {
        setError(response.data.message || 'Credenciais inválidas');
      }
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Erro ao conectar com o servidor. Verifique se o backend está rodando.');
      }
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-logo">
          <div className="logo-icon">
            <Shield size={32} />
          </div>
          <h1>Entrar no SGCO</h1>
        </div>
        
        {error && <div style={{ color: 'var(--danger)', marginBottom: '1rem', padding: '0.5rem', background: '#fee2e2', borderRadius: '8px' }}>{error}</div>}
        
        <form onSubmit={handleSubmit(onSubmit)} style={{ textAlign: 'left' }}>
          <div className="form-group">
            <label>Usuário</label>
            <input 
              type="text" 
              placeholder="Digite seu usuário (ex: admin)"
              {...register('username')} 
            />
            {errors.username && <span className="error-message">{errors.username.message}</span>}
          </div>
          <div className="form-group">
            <label>Senha</label>
            <input 
              type="password" 
              placeholder="Digite sua senha (ex: admin)"
              {...register('password')} 
            />
            {errors.password && <span className="error-message">{errors.password.message}</span>}
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={isSubmitting}>
            {isSubmitting ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;
