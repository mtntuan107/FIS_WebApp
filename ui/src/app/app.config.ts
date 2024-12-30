import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideClientHydration} from '@angular/platform-browser';
import {provideHttpClient} from '@angular/common/http';
import {KeycloakAuthService} from './shared/services/keycloak.service';
import {KeycloakService} from 'keycloak-angular';
import {AuthGuard} from './auth.guard';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideClientHydration(),
    provideHttpClient(),
    importProvidersFrom(KeycloakAuthService, KeycloakService),
    AuthGuard,
    KeycloakService,
    KeycloakAuthService,
  ]
};
