import { apiClient } from './client';
import { ApiResponse, ApplicationDetail, ApplicationSummary } from '../types';

export interface ApplicationFilterParams {
  page?: number;
  per_page?: number;
  status?: string;
  wave_id?: number | string;
  study_program_id?: number | string;
}

export async function fetchAdminApplications(
  params?: ApplicationFilterParams
): Promise<ApiResponse<ApplicationSummary[]>> {
  const response = await apiClient.get<ApiResponse<ApplicationSummary[]>>('/admin/applications', {
    params,
  });
  return response.data;
}

export async function fetchAdminApplicationDetail(
  id: number
): Promise<ApiResponse<ApplicationDetail>> {
  const response = await apiClient.get<ApiResponse<ApplicationDetail>>(`/admin/applications/${id}`);
  return response.data;
}
