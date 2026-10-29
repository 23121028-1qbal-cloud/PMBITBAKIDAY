import { apiClient, bootstrapCsrf } from './client';
import { ApiResponse, User } from '../types';

export interface LoginResponseData {
  user: User;
}

export async function sessionLogin(email: string, password: string): Promise<User> {
  // 1. Bootstrap CSRF cookie from Laravel Sanctum
  await bootstrapCsrf();

  // 2. Perform session-based login
  const response = await apiClient.post<ApiResponse<LoginResponseData>>('/auth/session/login', {
    email,
    password,
  });

  return response.data.data.user;
}

export async function sessionLogout(): Promise<void> {
  await apiClient.post<ApiResponse<[]>>('/auth/session/logout');
}

export async function getMe(): Promise<User> {
  const response = await apiClient.get<ApiResponse<User>>('/auth/me');
  return response.data.data;
}
