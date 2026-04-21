import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Agent, ApiResponse } from '../models/api.models';

interface ApiEnvelope<T> {
  data: T;
}

@Injectable({
  providedIn: 'root'
})
export class AgentApiService {
  private readonly baseUrl = `${environment.apiBaseUrl}/v1`;

  constructor(private http: HttpClient) {}

  listAgents(active = true): Observable<ApiResponse<Agent[]>> {
    return this.http
      .get<ApiEnvelope<Agent[]>>(`${this.baseUrl}/agents`, { params: { active } })
      .pipe(map(({ data }) => ({ data })));
  }

  getAgentByCode(code: string): Observable<ApiResponse<Agent>> {
    return this.http
      .get<ApiEnvelope<Agent>>(`${this.baseUrl}/agents/${code}`)
      .pipe(map(({ data }) => ({ data })));
  }
}
