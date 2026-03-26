import React from 'react';
import { NavLink, Outlet } from 'react-router-dom';
import { Activity, Users, Settings, FileText, Shield, Search, LogOut } from 'lucide-react';

const MainLayout = ({ onLogout }) => {
  return (
    <div className="app-container">
      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-header">
          <div className="logo-icon">
            <Shield />
          </div>
          <h1 className="logo-text">SGCO</h1>
        </div>
        <nav className="nav-menu">
          <NavLink to="/" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} end>
            <Activity /> Dashboard
          </NavLink>
          <NavLink to="/pacientes" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <Users /> Pacientes
          </NavLink>
          <NavLink to="/procedimentos" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <Settings /> Procedimentos
          </NavLink>
          <NavLink to="/documentos" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
            <FileText /> Documentos
          </NavLink>
          <button onClick={onLogout} className="nav-item" style={{ width: '100%', background: 'transparent', border: 'none', textAlign: 'left', marginTop: 'auto' }}>
            <LogOut /> Sair
          </button>
        </nav>
      </aside>

      {/* Main Content */}
      <main className="main-content">
        {/* Header */}
        <header className="header">
          <div className="search-bar">
            <Search size={18} />
            <input type="text" placeholder="Buscar paciente ou CPF..." />
          </div>
          <div className="user-profile">
            <div className="user-info">
              <div className="user-name">Dr. Ricardo Silva</div>
              <div className="user-role">Odontologista Geral</div>
            </div>
            <div className="avatar">RS</div>
          </div>
        </header>

        {/* Scrollable Content Area */}
        <div className="content-area">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;
