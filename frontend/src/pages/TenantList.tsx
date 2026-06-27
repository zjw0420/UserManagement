import { useEffect, useState, useCallback } from 'react';
import { clientApi, type ClientVo } from '../api/tenant';

const statusMap: Record<number, string> = { 1: '正常', 0: '禁用' };

export default function TenantList() {
  const [list, setList] = useState<ClientVo[]>([]);
  const [total, setTotal] = useState(0);
  const [pageNum, setPageNum] = useState(1);
  const [keyword, setKeyword] = useState('');
  const pageSize = 10;

  const load = useCallback(async () => {
    try {
      const res = await clientApi.page(pageNum, pageSize, keyword || undefined);
      const data = res.data;
      setList(data?.records ?? []);
      setTotal(data?.total ?? 0);
    } catch { /* */ }
  }, [pageNum, keyword]);

  useEffect(() => { load(); }, [load]);

  const totalPages = Math.ceil(total / pageSize);

  return (
    <div>
      <h1 className="page-title">租客管理</h1>
      <div className="toolbar">
        <input className="search-input" placeholder="🔍 搜索昵称/手机号..." value={keyword} onChange={e => { setKeyword(e.target.value); setPageNum(1); }} />
      </div>
      <table className="data-table">
        <thead><tr><th>ID</th><th>昵称</th><th>手机号</th><th>状态</th><th>注册时间</th></tr></thead>
        <tbody>
          {list.length === 0 ? <tr><td colSpan={5} className="empty-cell">暂无数据</td></tr> : list.map(c => (
            <tr key={c.id}>
              <td>{c.id}</td><td>{c.nickname}</td><td>{c.phone}</td>
              <td><span className={`tag ${c.status === 1 ? 'tag-active' : 'tag-inactive'}`}>{statusMap[c.status] ?? c.status}</span></td>
              <td>{c.createTime?.slice(0, 10)}</td>
            </tr>
          ))}
        </tbody>
      </table>
      {totalPages > 1 && (
        <div className="pagination">
          <button disabled={pageNum <= 1} onClick={() => setPageNum(pageNum - 1)}>上一页</button>
          <span>{pageNum} / {totalPages}</span>
          <button disabled={pageNum >= totalPages} onClick={() => setPageNum(pageNum + 1)}>下一页</button>
        </div>
      )}
    </div>
  );
}
