import { apiClient } from './client';
import { ApiResponse } from '../types';

export interface StartReviewPayload {
  expected_status: string;
}

export interface RequestRevisionPayload {
  expected_status: string;
  note: string;
}

export interface DecisionPayload {
  expected_status: string;
  decision: 'accepted' | 'rejected';
  note: string;
}

export async function submitStartReview(
  id: number,
  payload: StartReviewPayload
): Promise<ApiResponse<{ id: number; status: string }>> {
  const response = await apiClient.post<ApiResponse<{ id: number; status: string }>>(
    `/admin/applications/${id}/review`,
    payload
  );
  return response.data;
}

export async function submitRequestRevision(
  id: number,
  payload: RequestRevisionPayload
): Promise<ApiResponse<{ id: number; status: string; note: string }>> {
  const response = await apiClient.post<ApiResponse<{ id: number; status: string; note: string }>>(
    `/admin/applications/${id}/request-revision`,
    payload
  );
  return response.data;
}

export async function submitDecision(
  id: number,
  payload: DecisionPayload
): Promise<ApiResponse<{ id: number; status: string; decided_at: string }>> {
  const response = await apiClient.post<ApiResponse<{ id: number; status: string; decided_at: string }>>(
    `/admin/applications/${id}/decision`,
    payload
  );
  return response.data;
}
