import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet],
  template: `
    <div class="main-layout">
      <header class="header">
        <div class="header-container">
          <div class="logo">
            <h1>Cotizador de Daños</h1>
          </div>
          <nav class="nav-menu">
            <ul>
              <li><a href="/">Cotizaciones</a></li>
              <li><a href="/">Panel</a></li>
            </ul>
          </nav>
        </div>
      </header>

      <div class="layout-body">
        <main class="main-content">
          <router-outlet></router-outlet>
        </main>
      </div>

      <footer class="footer">
        <p>&copy; 2024 Sistema de cotización de seguros. Todos los derechos reservados.</p>
      </footer>
    </div>
  `,
  styles: [`
    .main-layout {
      display: flex;
      flex-direction: column;
      min-height: 100vh;
    }

    .header {
      background-color: #2c3e50;
      color: white;
      padding: 1rem 0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .header-container {
      max-width: 1400px;
      margin: 0 auto;
      padding: 0 1rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .logo h1 {
      margin: 0;
      font-size: 1.5rem;
      font-weight: 600;
    }

    .nav-menu ul {
      list-style: none;
      margin: 0;
      padding: 0;
      display: flex;
      gap: 2rem;
    }

    .nav-menu a {
      color: white;
      text-decoration: none;
      font-weight: 500;
      transition: color 0.3s;
    }

    .nav-menu a:hover {
      color: #3498db;
    }

    .layout-body {
      flex: 1;
      max-width: 1400px;
      width: 100%;
      margin: 0 auto;
      padding: 2rem 1rem;
    }

    .main-content {
      background-color: white;
      border-radius: 8px;
      padding: 2rem;
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
    }

    .footer {
      background-color: #2c3e50;
      color: white;
      text-align: center;
      padding: 2rem;
      margin-top: auto;
    }

    .footer p {
      margin: 0;
    }

    @media (max-width: 768px) {
      .header-container {
        flex-direction: column;
        gap: 1rem;
      }

      .nav-menu ul {
        gap: 1rem;
      }

      .layout-body {
        padding: 1rem;
      }

      .main-content {
        padding: 1rem;
      }
    }
  `]
})
export class MainLayoutComponent {}
