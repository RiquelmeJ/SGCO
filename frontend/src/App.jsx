import React, { useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainLayout from './layouts/MainLayout';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Pacientes from './pages/Pacientes';
import Procedimentos from './pages/Procedimentos';
import Documentos from './pages/Documentos';

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(
    localStorage.getItem('auth') === 'true'
  );

  const handleLogin = () => {
    localStorage.setItem('auth', 'true');
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('auth');
    setIsAuthenticated(false);
  };

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={
          !isAuthenticated ? <Login onLogin={handleLogin} /> : <Navigate to="/" />
        } />
        
        <Route path="/" element={
          isAuthenticated ? <MainLayout onLogout={handleLogout} /> : <Navigate to="/login" />
        }>
          <Route index element={<Dashboard />} />
          <Route path="pacientes" element={<Pacientes />} />
          <Route path="procedimentos" element={<Procedimentos />} />
          <Route path="documentos" element={<Documentos />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
